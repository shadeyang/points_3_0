package com.wt2024.points.dubbo.backend.utils.ext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/4/7 16:56
 * @Project points2.0:com.wt2024.points.service.utils.ext
 */
class EncryptUtilsTest {
    private static final String KEY = "o7H8uIM2O5qv65l2";

    @Test
    public void aesEncryptTest() throws Exception {
        String encrypt = EncryptUtils.aesEncrypt("TestString么么哒", KEY);
        Assertions.assertEquals("gMOlJ7K9X092OO//HGSrbMhIKOTGkjIMqJOEz8Yrt1s=", encrypt);
    }

    @Test
    public void aesDecryptTest() throws Exception {
        String decrypt = EncryptUtils.aesDecrypt("gMOlJ7K9X092OO//HGSrbMhIKOTGkjIMqJOEz8Yrt1s=", KEY);
        Assertions.assertEquals("TestString么么哒", decrypt);
    }

}