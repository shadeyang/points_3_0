package com.wt2024.points.dubbo.backend.utils.auth;

import java.io.File;
import java.io.FileWriter;

/**
 * @ClassName GenRSAKey
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/4
 * @Version V1.0
 **/
public class GenRSAKey {
    private static final String client_prikey_file = "client_prikey.dat";
    private static final String client_pubkey_file = "client_pubkey.dat";

    private static final String server_prikey_file = "server_prikey.dat";
    private static final String server_pubkey_file = "server_pubkey.dat";

    private static final String text_file = "readme.txt";

    private static final String bankcode = "10084";
    private static final String custno = "8888888";
    private static final String model = "dev";

    public void genSecretKeyTest() throws Exception {
        String path = "keys" + File.separator + bankcode + File.separator + model + File.separator;
//      第一种用法：公钥加密，私钥解密。---用于加解密
//      第二种用法：私钥签名，公钥验签。---用于签名
        String client_threeDesKey = threeDesKey();
        String server_threeDesKey = threeDesKey();

        File file = new File(path);
        //判断是否存在
        while (!file.exists()) {
            file.mkdirs();
        }

        FileWriter fw = new FileWriter(path + text_file);
        fw.write("clientthreeDesKey:" + client_threeDesKey + "\r\nserverthreeDesKey:" + server_threeDesKey);
        fw.close();

        RSAManager.genKeyToFile(path + client_prikey_file, client_threeDesKey, path + client_pubkey_file);
        RSAManager.genKeyToFile(path + server_prikey_file, server_threeDesKey, path + server_pubkey_file);
        //存发起端私钥，服务端公钥
        String client_privateKey = RSAManager.getPrivateKeyFromFile(path + client_prikey_file, client_threeDesKey);
        String client_privateTemp = RSAManager.getPublicKeyFromFile(path + client_prikey_file);
        String server_publicKey = RSAManager.getPublicKeyFromFile(path + server_pubkey_file);

//        BankSysInfoEntity bankSysInfoEntity = new BankSysInfoEntity();
//        bankSysInfoEntity.setBankcode(bankcode);
//        bankSysInfoEntity.setCustno(custno);
//        bankSysInfoEntity.setBanktype("1");
//        bankSysInfoEntity.setPrivatekey(client_privateTemp);
//        bankSysInfoEntity.setPublickey(server_publicKey);
//        bankSysInfoEntity.setThreedeskey(client_threeDesKey);
//        bankSysInfoMapper.insert(bankSysInfoEntity);
    }

    public String threeDesKey() {
        String deskey = "";
        for (int i = 0; i < 24; i++) {
            deskey += (int) ((Math.random() * 9 + 1));
        }
        return deskey;
    }
}
