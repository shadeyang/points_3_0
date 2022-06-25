package com.wt2024.points.dubbo.backend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wt2024.points.dubbo.backend.constant.Constants;
import com.wt2024.points.dubbo.backend.domain.Base;
import com.wt2024.points.dubbo.backend.restful.JsonResult;
import com.wt2024.points.dubbo.backend.retcode.MsgRetCode;
import com.wt2024.points.dubbo.backend.utils.auth.RSAManager;
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

    public JsonResult<T> httpPostWithJson(Base jsonObj, String url, String institutionNo, String serverPrivateKey, String clientPublicKey) {

        JsonResult<T> jsonResult = new JsonResult(false, MsgRetCode.COMM_9000.getRetCode(), MsgRetCode.COMM_9000.getRetShow(), "未知错误");

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
            String signData = institutionNo + timestamp + jsonObj.toString();
            log.debug("signData={}", signData);

            String sign = RSAManager.sign(signData.getBytes(charsetName), serverPrivateKey);
            post.setHeader(Constants.CONSTANT_HEADER_SIGN, sign);
//            post.setHeader(Constants.CONSTANT_HEADER_SIGN, "1"+sign.substring(1));

            log.debug("sign={}", sign);

            // 构建消息实体
            StringEntity entity = new StringEntity(jsonObj.toString(), Charset.forName(charsetName));
            entity.setContentEncoding(charsetName);
            // 发送Json格式的数据请求
            entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            post.setEntity(entity);

            HttpResponse response = httpClient.execute(post);

            // 检验返回码
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.warn("请求出错: " + statusCode);
                jsonResult.setMsgRetCode(MsgRetCode.COMM_9000, ("httpCode" + statusCode));
                return jsonResult;
            } else {
                String resBody = EntityUtils.toString(response.getEntity());
                /** 将String数组转为JSON*/
                log.debug("返回: " + resBody);
                ObjectMapper mapper = new ObjectMapper();
                jsonResult = mapper.readValue(resBody, JsonResult.class);

                T data = mapper.readValue(mapper.writeValueAsString(jsonResult.getData()), entityClass);
                jsonResult.setData(data);

                String retB = response.getFirstHeader(Constants.CONSTANT_HEADER_INSTITUTION).getValue();
                String retTimestamp = response.getFirstHeader(Constants.CONSTANT_HEADER_TIMESTAMP).getValue();
                String retSignData = retB + retTimestamp + resBody;
                log.debug("retSignData: " + retSignData);

                Header signs = response.getFirstHeader(Constants.CONSTANT_HEADER_SIGN);
                if (signs != null) {
                    String retsign = signs.getValue();
                    boolean l = RSAManager.verify(retSignData.getBytes(charsetName), clientPublicKey, retsign);
                    if (!l) {
                        log.error("签名验证失败");
                        jsonResult.setMsgRetCode(MsgRetCode.COMM_9000, "签名验证失败");
                        return jsonResult;
                    }
                }
                return jsonResult;
            }
        } catch (Exception e) {
            log.error("请求交易失败", e);
            jsonResult.setMsgRetCode(MsgRetCode.COMM_9000, "请求交易失败" + e.getMessage());
            return jsonResult;
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
    }

}
