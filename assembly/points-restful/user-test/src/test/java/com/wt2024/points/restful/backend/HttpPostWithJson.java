package com.wt2024.points.restful.backend;

import com.wt2024.points.core.api.domain.InputBase;
import com.wt2024.points.restful.backend.constant.Constants;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import com.wt2024.points.restful.backend.utils.HttpPostUtils;
import com.wt2024.points.restful.backend.utils.auth.RSAManager;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2019-01-09 19:36
 * @Project openapi:com.uxunchina.openapi.httppost
 */
@Slf4j
public class HttpPostWithJson {

    public static final String POINTS = "http://127.0.0.1:9070/points";

    public static <T> ResponseResult<T> httpPostWithJson(InputBase input, String url, Class<T> clazz) throws Exception {
        String serverPrivateKey = RSAManager.getPrivateKeyFromFile(HttpPostWithJson.class.getClassLoader().getResource(Constants.SERVER_PRIVATE_KEY_FILE).getPath(), Constants.SERVER_THREE_DES_KEY);
        String clientPublicKey = RSAManager.getPublicKeyFromFile(HttpPostWithJson.class.getClassLoader().getResource(Constants.CLIENT_PUBLIC_KEY_FILE).getPath());

        ResponseResult result = new HttpPostUtils(clazz).httpPostWithJson(input, POINTS + url, input.getInstitutionNo(), serverPrivateKey, clientPublicKey);
        log.info("result={}", result);
        return result;
    }

    // 构建唯一会话Id
    public static String getSessionId() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
    }

}
