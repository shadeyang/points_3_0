package com.wt2024.points.dubbo.backend.utils.auth;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/4/7 17:19
 * @Project points2.0:com.wt2024.points.service.utils.auth
 */
public class RSAManager {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public RSAManager() {
    }

    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(priKey);
        signature.update(data);
        return new String(Base64.encode(signature.sign()));
    }

    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(pubKey);
        signature.update(data);
        return signature.verify(Base64.decode(sign));
    }

    public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64.decode(key);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(2, privateKey);
        byte[] decData = (byte[]) null;
        byte[] DataBlock = (byte[]) null;
        decData = new byte[117 * (data.length / 128 + 1)];
        int realLen = 0;

        for (int i = 0; i < data.length / 128; ++i) {
            int len = (i + 1) * 128 >= data.length ? data.length - i * 128 : 128;
            DataBlock = new byte[len];
            System.arraycopy(data, i * 128, DataBlock, 0, len);
            byte[] decDataBlock = cipher.doFinal(DataBlock);
            System.arraycopy(decDataBlock, 0, decData, i * 117, decDataBlock.length);
            realLen += decDataBlock.length;
        }

        byte[] decRData = new byte[realLen];
        System.arraycopy(decData, 0, decRData, 0, realLen);
        return decRData;
    }

    public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64.decode(key);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(2, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64.decode(key);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(1, publicKey);
        byte[] encData = (byte[]) null;
        byte[] DataBlock = (byte[]) null;
        encData = new byte[128 * (data.length / 117 + 1)];

        for (int i = 0; i <= data.length / 117; ++i) {
            int len = (i + 1) * 117 >= data.length ? data.length - i * 117 : 117;
            DataBlock = new byte[len];
            System.arraycopy(data, i * 117, DataBlock, 0, len);
            byte[] encDataBlock = cipher.doFinal(DataBlock);
            System.arraycopy(encDataBlock, 0, encData, i * 128, encDataBlock.length);
        }

        return encData;
    }

    public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64.decode(key);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(1, privateKey);
        return cipher.doFinal(data);
    }

    public static void genKeyToFile(String prikeyfile, String deskey, String pubkeyfile) throws Exception {
        Map keyMap = initKey();
        String publicKey = getPublicKey(keyMap);
        OutputStream encout = new FileOutputStream(prikeyfile);
        Key key = (Key) keyMap.get("RSAPrivateKey");
        byte[] encdata = Des3Tool.encryptData(deskey, key.getEncoded());
        encout.write(Base64.encode(encdata));
        encout.flush();
        encout.close();
        OutputStream out = new FileOutputStream(pubkeyfile);
        out.write(publicKey.getBytes());
        out.flush();
        out.close();
    }

    public static String getPrivateKeyFromFile(String prikeyfile, String deskey) throws Exception {
        InputStream in = new FileInputStream(prikeyfile);
        byte[] data = new byte[2048];
        int iLen = in.read(data);
        byte[] newdata = new byte[iLen];
        System.arraycopy(data, 0, newdata, 0, iLen);
        in.close();
        byte[] encdata = Base64.decode(newdata);
        data = Des3Tool.decryptData(deskey, encdata);
        return new String(Base64.encode(data));
    }

    public static String getPublicKeyFromFile(String pubkeyfile) throws Exception {
        InputStream in = new FileInputStream(pubkeyfile);
        byte[] data = new byte[2048];
        int iLen = in.read(data);
        byte[] newdata = new byte[iLen];
        System.arraycopy(data, 0, newdata, 0, iLen);
        in.close();
        return new String(newdata, "UTF-8");
    }

    public static String getPrivateKey(Map keyMap) throws Exception {
        Key key = (Key) keyMap.get("RSAPrivateKey");
        return new String(Base64.encode(key.getEncoded()), "UTF-8");
    }

    public static String getPublicKey(Map keyMap) throws Exception {
        Key key = (Key) keyMap.get("RSAPublicKey");
        return new String(Base64.encode(key.getEncoded()), "UTF-8");
    }

    public static Map initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map keyMap = new HashMap(2);
        keyMap.put("RSAPublicKey", publicKey);
        keyMap.put("RSAPrivateKey", privateKey);
        return keyMap;
    }
}
