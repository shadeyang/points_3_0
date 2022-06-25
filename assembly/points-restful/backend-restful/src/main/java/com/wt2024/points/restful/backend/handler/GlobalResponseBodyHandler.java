package com.wt2024.points.restful.backend.handler;

import com.wt2024.points.restful.backend.constant.Constants;
import com.wt2024.points.restful.backend.constant.LocalConstants;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import com.wt2024.points.restful.backend.domain.SysInfo;
import com.wt2024.points.restful.backend.utils.auth.RSAManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/9 09:43
 * @project points3.0:com.wt2024.points.restful.backend.advice
 */
@RestControllerAdvice
@Slf4j
public class GlobalResponseBodyHandler implements ResponseBodyAdvice<Object> {

    private static final String ENCODING_UTF_8 = StandardCharsets.UTF_8.toString();

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ResponseResult) {
            String institutionNo = null;
            if (request.getHeaders().containsKey(Constants.CONSTANT_HEADER_INSTITUTION)) {
                institutionNo = request.getHeaders().get(Constants.CONSTANT_HEADER_INSTITUTION).stream().findFirst().orElse("");
            }
            SysInfo sysInfoDomain = null;
            if (StringUtils.isNoneBlank(institutionNo)) {
                sysInfoDomain = LocalConstants.sysInfoDomainHashMap.get(institutionNo);
            }
            addSign(response, (ResponseResult) body, sysInfoDomain, institutionNo);
        }
        return body;
    }

    private void addSign(ServerHttpResponse response, ResponseResult body, SysInfo sysInfoDomain, String institutionNo) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        response.getHeaders().put(Constants.CONSTANT_HEADER_INSTITUTION, Arrays.asList(institutionNo));
        response.getHeaders().put(Constants.CONSTANT_HEADER_TIMESTAMP, Arrays.asList(timestamp));

        if (sysInfoDomain == null || (StringUtils.isEmpty(sysInfoDomain.getPrivatekey()) && StringUtils.isEmpty(sysInfoDomain.getCustrequestip()))) {
            log.warn("不存在私钥配置，跳过签名计算");
        } else {
            if (StringUtils.isNotEmpty((sysInfoDomain.getPrivatekey()))) {
                try {
                    String signData = institutionNo + timestamp + body.toString();
                    log.debug("返回加签data={}", signData);
                    String sign = RSAManager.sign(signData.getBytes(ENCODING_UTF_8), sysInfoDomain.getPrivatekey());
                    log.debug("返回签名sign={}", sign);
                    response.getHeaders().put(Constants.CONSTANT_HEADER_SIGN, Arrays.asList(sign));
                } catch (Exception e1) {
                    log.error("加签处理错误，跳过签名计算", e1);
                }
            }
        }
    }

}
