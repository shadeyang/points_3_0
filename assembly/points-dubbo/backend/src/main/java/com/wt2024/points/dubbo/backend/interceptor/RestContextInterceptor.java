package com.wt2024.points.dubbo.backend.interceptor;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2019-01-08 21:41
 */

import com.wt2024.base.Parameter;
import com.wt2024.base.entity.SysParameter;
import com.wt2024.framework.constant.Constant;
import com.wt2024.framework.context.ServiceContext;
import com.wt2024.points.dubbo.backend.constant.Constants;
import com.wt2024.points.dubbo.backend.constant.LocalConstants;
import com.wt2024.points.dubbo.backend.domain.SysInfo;
import com.wt2024.points.dubbo.backend.exception.TransIOException;
import com.wt2024.points.dubbo.backend.restful.JsonResult;
import com.wt2024.points.dubbo.backend.retcode.MsgRetCode;
import com.wt2024.points.dubbo.backend.utils.auth.RSAManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;


@Provider
@Slf4j
public class RestContextInterceptor implements ContainerRequestFilter, WriterInterceptor, ContainerResponseFilter, ExceptionMapper<Exception> {

    private static final String ENCODING_UTF_8 = CharEncoding.UTF_8;
    public static final String NOT_WRAP_RESULT = "Not-Wrap-Result";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    private static ObjectMapper mapper = new ObjectMapper();

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    private static String[] whitePathList = {"index"};

    @Configuration
    static class Contextpath {

        public static String restContextpath;

        @Value("${dubbo.protocols.rest.contextpath}")
        public void setRestContextpath(String restContextpath) {
            Contextpath.restContextpath = restContextpath;
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        log.debug("进入请求拦截——filter");
        // 编码处理
        request.setCharacterEncoding(ENCODING_UTF_8);
        response.setCharacterEncoding(ENCODING_UTF_8);
        // * 表示允许任何域名跨域访问
        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        request.setAttribute(InputPart.DEFAULT_CHARSET_PROPERTY, ENCODING_UTF_8);
        requestContext.setProperty(InputPart.DEFAULT_CHARSET_PROPERTY, ENCODING_UTF_8);

        // 客户端head显示提醒不要对返回值进行封装
        requestContext.setProperty(NOT_WRAP_RESULT, this.getWrapTag(requestContext));

        this.checkRequest(requestContext);
        // 请求参数打印
        this.logRequest(request);
    }


    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {

        log.debug("进入结果处理——aroundWriteTo");
        String institutionNo = request.getHeader(Constants.CONSTANT_HEADER_INSTITUTION);
        String timestamp = String.valueOf(System.currentTimeMillis());
        response.setHeader(Constants.CONSTANT_HEADER_INSTITUTION, institutionNo);
        response.setHeader(Constants.CONSTANT_HEADER_TIMESTAMP, timestamp);
//        针对需要封装的请求对结构进行封装处理。这里需要注意的是对返回类型已经是封装类(比如：异常处理器的响应可能已经是封装类型)时要忽略掉。
        Object originalObj = context.getEntity();
        // 已经被封装过了的,不用再次封装
        Boolean wraped = originalObj instanceof JsonResult;
        if (StringUtils.isBlank(this.getWrapTag(context))) {
            JsonResult<Object> result = new JsonResult<>(true, MsgRetCode.COMM_0000);
            if (wraped) {
                result = (JsonResult) originalObj;
            } else {
                // 判断是否为标准dubbo通讯实体,如果是则需要处理响应码
                /** JsonResult 状态码为通讯状态 响应成功即为正常，业务状态码以内部处理为准 **/
//                if (context.getEntity() instanceof BaseTransDomain) {
//                    String domainRetcode = ((BaseTransDomain) context.getEntity()).getRetcode();
//                    String domainRetmsg = ((BaseTransDomain) context.getEntity()).getRetmsg();
//                    if (StringUtils.isNotEmpty(domainRetcode))
//                        result.setRetcode(domainRetcode);
//                    if (StringUtils.isNotEmpty(domainRetmsg))
//                        result.setRetmsg(domainRetmsg);
//                }else
                if (originalObj instanceof LinkedHashMap) {
                    //匹配这个，为通讯层异常
                    LinkedHashMap linkedHashMap = (LinkedHashMap<String, Object>) originalObj;
                    Object retcode = linkedHashMap.get("retcode");
                    Object retmsg = linkedHashMap.get("retmsg");
                    if (retcode != null) {
                        result.setRetcode(retcode.toString());
                        result.setRetmsg(retmsg.toString());
                        result.setFlag(MsgRetCode.COMM_0000.getRetCode().equals(retcode.toString()));
                        result.setData(linkedHashMap.get("data"));
                    }
                } else {
                    result.setData(originalObj);
                }
            }
//            context.setEntity(result);
            //转化用于顺序排序，验证签名使用
            String jsonString = result.toString();
            LinkedHashMap<String, Object> json = mapper.readValue(jsonString, LinkedHashMap.class);
//            JSONObject jsonObject=new JSONObject(true);
//            jsonObject.putAll(json);
            context.setEntity(json);

            SysInfo sysInfoDomain = LocalConstants.sysInfoDomainHashMap.get(institutionNo);
            if (sysInfoDomain == null || (StringUtils.isEmpty(sysInfoDomain.getPrivatekey()) && StringUtils.isEmpty(sysInfoDomain.getCustrequestip()))) {
                log.warn("不存在私钥配置，跳过签名计算");
            } else {
                if (StringUtils.isNotEmpty((sysInfoDomain.getPrivatekey()))) {
                    try {
                        String signData = institutionNo + timestamp + jsonString;
                        log.debug("返回加签data={}", signData);
                        String sign = RSAManager.sign(signData.getBytes(ENCODING_UTF_8), sysInfoDomain.getPrivatekey());
                        log.debug("返回签名sign={}", sign);
                        response.setHeader(Constants.CONSTANT_HEADER_SIGN, sign);
                    } catch (Exception e1) {
                        log.error("加签处理错误，跳过签名计算", e1);
                    }
                }
            }

//          以下两处set避免出现Json序列化的时候,对象类型不符的错误
            context.setType(result.getClass());
            context.setGenericType(result.getClass().getGenericSuperclass());
        }
        context.proceed();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {

        log.debug("进入结果处理——filter");
//        它的目的是专门处理方法返回类型是 void,或者某个资源类型返回是 null 的情况,
//        这种情况下JAX-RS 框架一般会返回状态204,表示请求处理成功但没有响应内容。 我们对这种情况也重新处理改为操作成功
        if (StringUtils.isBlank(this.getWrapTag(requestContext)) && HttpStatus.SC_NO_CONTENT == responseContext.getStatus() && !responseContext.hasEntity()) {
            responseContext.setStatus(200);
            responseContext.setEntity(new JsonResult<>(true, MsgRetCode.COMM_0000));
            responseContext.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        }
    }

    /**
     * 异常拦截
     */
    @Override
    public Response toResponse(Exception e) {

        log.debug("进入结果处理——toResponse");

        String errMsg = e.getMessage();
        JsonResult<Object> result = new JsonResult<>(false, MsgRetCode.COMM_0011);

        if (e instanceof TransIOException) {
            TransIOException ex = (TransIOException) e;
            result = new JsonResult<>(false, ex.getMsgRetCode());
            log.warn(errMsg);
        } else {
            log.error(errMsg, e);
        }

        if (ClientErrorException.class.isAssignableFrom(e.getClass())) {
            ClientErrorException ex = (ClientErrorException) e;
            log.error("请求错误:" + e.getMessage());
            return ex.getResponse();
        }
        result.setData(null);

        return Response.status(200).entity(result).build();
    }


    private void checkRequest(ContainerRequestContext requestContext) throws IOException {

        String traceId = requestContext.getHeaderString(Constant.traceId);
        if (StringUtils.isEmpty(traceId)) {
            traceId = UUID.randomUUID().toString().replaceAll("-", "");
        }
        MDC.put(Constant.traceId, traceId);
        String localHost = "";
        String remoteHost = RestControllerInterceptor.getIpAddress(request);

        MDC.put(Constant.remoteHost, remoteHost);
        try {
            localHost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.debug("获取请求来源ip失败{}", e.toString());
        }
        MDC.put(Constant.localHost, localHost);
        //判断是否需要校验
        String path = requestContext.getUriInfo().getPath();
        String userId = "";
        if (StringUtils.isNotEmpty(path) && !isWhitePath(path)) {
            /**对于接口服务来说,银行编号即为用户,必须参数**/
            String institutionNo = requestContext.getHeaderString(Constants.CONSTANT_HEADER_INSTITUTION);
//            userId = userId + bankcode;
            MDC.put(Constant.userId, institutionNo);

            if (StringUtils.isEmpty(institutionNo)) {
                throw new TransIOException(MsgRetCode.COMM_0040, Constants.CONSTANT_HEADER_INSTITUTION);
            }

//        response.setHeader(Constants.CONSTANT_HEADER_BANKCODE,bankcode);
            //获取接入银行配置
            SysInfo sysInfoDomain = LocalConstants.sysInfoDomainHashMap.get(institutionNo);
            if (sysInfoDomain == null) {
                throw new TransIOException(MsgRetCode.COMM_0044, institutionNo);
            }
            //检查准入服务配置
            List<String> bankconf = sysInfoDomain.getSwitchmap().get(Constants.CONSTANT_REST);
            if (bankconf == null) {
                throw new TransIOException(MsgRetCode.COMM_0042, institutionNo);
            } else if (bankconf.size() == 0) {
                //此情况代表全量通过
            } else {
                //获取请求接口名
                String URI = requestContext.getUriInfo().getPath();
                //使用正则匹配
                boolean cando = false;
                for (String pattern : bankconf) {
                    if (Pattern.matches(pattern, URI)) {
                        log.debug("URI{}通过准入校验{}", URI, pattern);
                        cando = true;
                        break;
                    }
                }
                if (!cando) {
                    throw new TransIOException(MsgRetCode.COMM_0043, institutionNo, URI);
                }
            }

            /**对于接口服务来说,时间戳用于交易有效性校验,必须参数**/
            String timestampStr = requestContext.getHeaderString(Constants.CONSTANT_HEADER_TIMESTAMP);
            if (StringUtils.isEmpty(timestampStr)) {
                throw new TransIOException(MsgRetCode.COMM_0040, Constants.CONSTANT_HEADER_TIMESTAMP);
            }
            long timestamp = Long.parseLong(timestampStr);
            if (System.currentTimeMillis() - timestamp > Constants.CONSTANT_HEADER_TIMESTAMP_EXPIRE) {
                throw new TransIOException(MsgRetCode.COMM_0041);
            }

            /**判断安全验证方式**/
            boolean checkflag = false;

            if (StringUtils.isNotEmpty(sysInfoDomain.getCustrequestip())) {
                checkflag = true;
                List<String> ips = Arrays.asList(sysInfoDomain.getCustrequestip().split(","));
                log.info("当前请求接入IP为{}", remoteHost);
                if (!ips.contains(remoteHost)) {
                    throw new TransIOException(MsgRetCode.COMM_0032, institutionNo);
                }
            }

            if (StringUtils.isNotEmpty(sysInfoDomain.getPublickey()) && StringUtils.isNotEmpty(sysInfoDomain.getPrivatekey())) {
                checkflag = true;
                /**验证签名**/
                String sign = requestContext.getHeaderString(Constants.CONSTANT_HEADER_SIGN);
                if (StringUtils.isEmpty(sign)) {
                    throw new TransIOException(MsgRetCode.COMM_0040, Constants.CONSTANT_HEADER_SIGN);
                }
                if (StringUtils.isEmpty(sysInfoDomain.getPublickey()) || StringUtils.isEmpty(sysInfoDomain.getPrivatekey())) {
                    throw new TransIOException(MsgRetCode.COMM_0046, institutionNo);
                }
                /**获取body**/
                try (InputStream entityStream = requestContext.getEntityStream();
                     ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = entityStream.read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }
                    baos.flush();
                    String body = baos.toString();
                    log.debug("获取到报文体body={}", body);
                    /**开始签名计算**/
                    SysParameter sysParameter = Parameter.getSysParameter("signCheck");
                    if (sysParameter != null && Constants.FLAG_NO.equalsIgnoreCase(sysParameter.getParamValue1())) {
                        log.info("当前不校验签名");
                    } else {
                        String signData = institutionNo + timestampStr + body;
                        log.debug("签名计算signData={}", signData);
                        try {
                            boolean verifySign = RSAManager.verify(signData.getBytes(ENCODING_UTF_8), sysInfoDomain.getPublickey(), sign);
                            if (!verifySign) {
                                throw new Exception("签名验证返回失败");
                            }
                        } catch (Exception e) {
                            log.error("签名验证异常", e);
                            throw new TransIOException(MsgRetCode.COMM_0045);
                        }
                    }
                    //放回到输入流
                    requestContext.setEntityStream(new ByteArrayInputStream(baos.toByteArray()));
                }
            }

            if (!checkflag) {
                log.info("机构{}缺失安全配置", institutionNo);
                //无任何安全设置
                throw new TransIOException(MsgRetCode.COMM_0044, institutionNo);
            }
        } else {
            MDC.put(Constant.userId, "UNKNOW");
        }

        ServiceContext context = ServiceContext.getContext();
        context.clearContextVar();
        context.addContextVar(Constant.traceId, traceId);
        context.addContextVar(Constant.remoteHost, remoteHost);
        context.addContextVar(Constant.localHost, localHost);
        context.addContextVar(Constant.userId, userId);
    }


    private void logRequest(HttpServletRequest request) {

        StringBuffer logBuffer = new StringBuffer(128);

        // refer_url
        logBuffer.append("referUrl:");

        @SuppressWarnings("rawtypes")
        Enumeration e = request.getHeaders("Referer");
        String referUrl;
        if (e.hasMoreElements()) {
            referUrl = (String) e.nextElement();
        } else {
            referUrl = "直接访问";
        }
        logBuffer.append(referUrl);
        // 获取url
        logBuffer.append(";URL:");
        StringBuffer url = request.getRequestURL();
        if (url != null) {
            StringUtils.replaceOnce(url.toString(), "http://", "https://");
        }
        logBuffer.append(url.toString());
        // 判断用户请求方式是否为ajax
        logBuffer.append(";ISAJAX:");
        String requestType = request.getHeader("X-Requested-With");
        if (StringUtils.isNotBlank(requestType) && requestType.equals("XMLHttpRequest")) {
            logBuffer.append("true");
        } else {
            logBuffer.append("false");
        }
        //获取所有参数
        StringBuffer paramBuffer = new StringBuffer(64);
        Enumeration<?> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            paramBuffer.append(paraName);
            paramBuffer.append(": ");
            paramBuffer.append(request.getParameter(paraName));
            paramBuffer.append(", ");
        }
        logBuffer.append(";Parameters:");
        logBuffer.append(paramBuffer);

        // 记录本次请求耗时：
//        Long requestEndTime = System.currentTimeMillis();
//        Long requestStartTime = StringUtils.isEmpty(MDC.get(REQUEST_STARTTIME)) ? requestEndTime : Long.valueOf(MDC.get(REQUEST_STARTTIME));

//        logBuffer.append(";COST:");
//        logBuffer.append(requestEndTime - requestStartTime);
//        logBuffer.append(" ms");
        if (!"HEAD".equals(request.getMethod())) {
            log.info("requestLog:" + logBuffer);
        }
    }

    private boolean isWhitePath(String path) {
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String contextPath = "/" + Contextpath.restContextpath;
        if (!contextPath.endsWith("/")) {
            contextPath = contextPath + "/";
        }
        for (String white : whitePathList) {
            if (path.equalsIgnoreCase(contextPath + white)) {
                return true;
            }
        }
        return false;
    }

    private String getWrapTag(ContainerRequestContext requestContext) {
        return requestContext.getProperty(NOT_WRAP_RESULT) == null ? "" : requestContext.getProperty(NOT_WRAP_RESULT).toString();
    }

    private CharSequence getWrapTag(WriterInterceptorContext context) {
        return context.getProperty(NOT_WRAP_RESULT) == null ? "" : context.getProperty(NOT_WRAP_RESULT).toString();
    }

}