package com.wt2024.points.restful.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wt2024.points.restful.backend.constant.Constants;
import com.wt2024.points.restful.backend.constant.TestConstants;
import com.wt2024.points.restful.backend.utils.auth.RSAManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/5/19 17:27
 * @Project points2.0:com.wt2024.points.service.test
 */
@ActiveProfiles("test")
@SpringBootTest
@Slf4j
public abstract class SpringBootContextTest {

    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void before() {
    }

    protected HttpHeaders createHttpHeaders(Object object) throws Exception {

        String institutionNo = TestConstants.DEFAULT_INSTITUTION_NO;
        String timestamp = String.valueOf(System.currentTimeMillis());

        String signData = institutionNo + timestamp + (Objects.isNull(object) ? "" : objectMapper.writeValueAsString(object));
        String path = this.getClass().getClassLoader().getResource(TestConstants.SERVER_PRIVATE_KEY_FILE).getPath();
        String serverPrivateKey = RSAManager.getPrivateKeyFromFile(path, TestConstants.SERVER_THREE_DES_KEY);
        String sign = RSAManager.sign(signData.getBytes(StandardCharsets.UTF_8), serverPrivateKey);
        log.info("signData={}, sign={}", signData, sign);
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.CONSTANT_HEADER_INSTITUTION, institutionNo);
        headers.add(Constants.CONSTANT_HEADER_TIMESTAMP, timestamp);
        headers.add(Constants.CONSTANT_HEADER_SIGN, sign);
        return headers;
    }
}
