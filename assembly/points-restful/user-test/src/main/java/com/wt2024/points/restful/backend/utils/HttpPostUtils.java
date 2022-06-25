package com.wt2024.points.restful.backend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.core.api.domain.InputBase;
import com.wt2024.points.restful.backend.constant.Constants;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import com.wt2024.points.restful.backend.utils.auth.RSAManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2019-01-13 15:24
 * @Project openapi:com.wt2024.points.httppost
 */
@Slf4j
public class HttpPostUtils<T> {

    private Class<T> entityClass;

    private String charsetName = CharEncoding.UTF_8;

    public HttpPostUtils(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public ResponseResult<T> httpPostWithJson(InputBase input, String url, String institutionNo, String serverPrivateKey, String clientPublicKey) {

        ResponseResult<T> jsonResult = ResponseResult.builder().build().fail(PointsCode.COMM_9000, "未知错误");

        HttpPost post = null;

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).setConnectionRequestTimeout(30000).build();

        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()) {

            post = new HttpPost(url);
            // 构造消息头
            post.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
            post.setHeader(HttpHeaders.CONNECTION, HTTP.CONN_CLOSE);
            String timestamp = String.valueOf(System.currentTimeMillis());
            post.setHeader(Constants.CONSTANT_HEADER_TIMESTAMP, timestamp);
            post.setHeader(Constants.CONSTANT_HEADER_INSTITUTION, institutionNo);
            String signData = institutionNo + timestamp + input.toString();
            log.debug("signData={}", signData);

            String sign = RSAManager.sign(signData.getBytes(charsetName), serverPrivateKey);
            post.setHeader(Constants.CONSTANT_HEADER_SIGN, sign);
//            post.setHeader(Constants.CONSTANT_HEADER_SIGN, "1"+sign.substring(1));

            log.debug("sign={}", sign);

            // 构建消息实体
            StringEntity entity = new StringEntity(input.toString(), Charset.forName(charsetName));
            entity.setContentEncoding(charsetName);
            // 发送Json格式的数据请求
            entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            post.setEntity(entity);

            HttpResponse response = httpClient.execute(post);

            // 检验返回码
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.warn("请求出错: " + statusCode);
                return jsonResult.fail(PointsCode.COMM_9000, ("httpCode" + statusCode));
            } else {
                String resBody = EntityUtils.toString(response.getEntity());
                /** 将String数组转为JSON*/
                log.debug("返回: " + resBody);
                ObjectMapper mapper = new ObjectMapper();
                jsonResult = mapper.readValue(resBody, ResponseResult.class);
                PointsCode pointsCode = PointsCode.matches(jsonResult.getCode());
                if (pointsCode == null) {
                    return jsonResult.fail(PointsCode.COMM_9000, "响应码非标");
                }
                if (PointsCode.COMM_0040.equals(pointsCode) || PointsCode.COMM_0044.equals(pointsCode)) {
                    return jsonResult;
                }
                T data = mapper.readValue(mapper.writeValueAsString(jsonResult.getData()), entityClass);
                jsonResult.setData(data);

                Header retInstitutionHeader = response.getFirstHeader(Constants.CONSTANT_HEADER_INSTITUTION);
                Header retTimestampHeader = response.getFirstHeader(Constants.CONSTANT_HEADER_TIMESTAMP);
                if (Objects.isNull(retInstitutionHeader) || Objects.isNull(retTimestampHeader)) {
                    log.error("签名验证失败");
                    return jsonResult.fail(PointsCode.COMM_9000, "签名验证失败");
                }
                String retSignData = retInstitutionHeader.getValue() + retTimestampHeader.getValue() + resBody;
                log.debug("retSignData: " + retSignData);

                Header signs = response.getFirstHeader(Constants.CONSTANT_HEADER_SIGN);
                if (signs != null) {
                    if (!RSAManager.verify(retSignData.getBytes(charsetName), clientPublicKey, signs.getValue())) {
                        log.error("签名验证失败");
                        return jsonResult.fail(PointsCode.COMM_9000, "签名验证失败");
                    }
                }
                return jsonResult;
            }
        } catch (Exception e) {
            log.error("请求交易失败", e);
            return jsonResult.fail(PointsCode.COMM_9000, "请求交易失败" + e.getMessage());
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
    }

}
