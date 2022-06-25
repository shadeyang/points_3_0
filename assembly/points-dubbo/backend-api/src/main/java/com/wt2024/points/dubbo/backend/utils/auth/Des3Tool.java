package com.wt2024.points.dubbo.backend.utils.auth;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/4/7 17:22
 * @Project points2.0:com.wt2024.points.service.utils.auth
 */
public class Des3Tool {
    private static final String ALGORITHM = "DESede";

    public Des3Tool() {
    }

    public static CipherOutputStream encryptMode(SecretKey secretKey, OutputStream outputStream) {
        try {
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(1, secretKey);
            return new CipherOutputStream(outputStream, c1);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static CipherOutputStream encryptMode(String des3Key, OutputStream outputStream) {
        SecretKey secretKey = new SecretKeySpec(des3Key.getBytes(), "DESede");
        return encryptMode((SecretKey) secretKey, outputStream);
    }

    public static byte[] encryptData(String des3Key, byte[] srcdata) {
        SecretKeySpec secretKey = new SecretKeySpec(des3Key.getBytes(), "DESede");

        try {
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(1, secretKey);
            return c1.doFinal(srcdata);
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static CipherInputStream decryptMode(String des3Key, InputStream inputStream) {
        SecretKey deskey = new SecretKeySpec(des3Key.getBytes(), "DESede");
        return decryptMode((SecretKey) deskey, inputStream);
    }

    public static CipherInputStream decryptMode(SecretKey secretKey, InputStream inputStream) {
        try {
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(2, secretKey);
            return new CipherInputStream(inputStream, c1);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static byte[] decryptData(String des3Key, byte[] data) {
        SecretKeySpec deskey = new SecretKeySpec(des3Key.getBytes(), "DESede");

        try {
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(2, deskey);
            return c1.doFinal(data);
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }
}
