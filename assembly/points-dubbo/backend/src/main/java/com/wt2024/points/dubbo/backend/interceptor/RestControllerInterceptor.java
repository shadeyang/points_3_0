package com.wt2024.points.dubbo.backend.interceptor;

import com.wt2024.base.Parameter;
import com.wt2024.base.entity.SysParameter;
import com.wt2024.framework.constant.Constant;
import com.wt2024.framework.context.ServiceContext;
import com.wt2024.framework.context.Wt2024IOTransfer;
import com.wt2024.framework.context.Wt2024Limit;
import com.wt2024.framework.context.Wt2024Monitor;
import com.wt2024.framework.web.interceptor.LimitConfig;
import com.wt2024.points.dubbo.backend.constant.Constants;
import com.wt2024.points.dubbo.backend.constant.LocalConstants;
import com.wt2024.points.dubbo.backend.domain.SysInfo;
import com.wt2024.points.dubbo.backend.exception.TransIOException;
import com.wt2024.points.dubbo.backend.restful.JsonResult;
import com.wt2024.points.dubbo.backend.retcode.MsgRetCode;
import com.wt2024.points.dubbo.backend.utils.auth.RSAManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * springmvc.xml 增加web拦截器配置
 * <mvc:interceptors>
 * <bean class="com.uxunchina.framework.web.interceptor.WebInterceptor"></bean>
 * </mvc:interceptors>
 * <p>
 * struts.xml 增加web拦截器配置
 * <interceptor name="webInterceptor" class="com.uxunchina.framework.web.interceptor.WebInterceptor"></interceptor>
 *
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/5/22 01:12
 * @Project uxun-framework:com.uxunchina.framework.web.interceptor
 */
@Slf4j
public class RestControllerInterceptor extends HandlerInterceptorAdapter {

    private static final String ENCODING_UTF_8 = "UTF-8";

    private String flag = "F";

    private static final Logger transLogger = LoggerFactory.getLogger(Wt2024IOTransfer.class);

    private static final Logger monitorLogger = LoggerFactory.getLogger(Wt2024Monitor.class);

    private static final Logger limitLogger = LoggerFactory.getLogger(Wt2024Limit.class);

    /**
     * 限流开关
     **/
    public static boolean limitFlag = false;
    /**
     * 生产模式，为fasle只走逻辑，不进行阻断
     **/
    public static boolean testModel = true;

    public static List<String> limitKey = new ArrayList<String>();

    public static Map<String, LimitConfig> limitConfigMap = new HashMap<String, LimitConfig>();

    public static String errorUrl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        long startTime = System.currentTimeMillis();
        request.setAttribute(Constant.startTime, startTime);
        try {
            // 编码处理
            request.setCharacterEncoding(ENCODING_UTF_8);
            response.setCharacterEncoding(ENCODING_UTF_8);
            // * 表示允许任何域名跨域访问
            response.setHeader("Access-Control-Allow-Origin", "*");
            request.setAttribute(InputPart.DEFAULT_CHARSET_PROPERTY, ENCODING_UTF_8);

            ServiceContext context = ServiceContext.getContext();

            String traceId = request.getHeader(Constant.traceId);
            if (StringUtils.isEmpty(traceId)) {
                traceId = UUID.randomUUID().toString().replaceAll("-", "");
            }

            String localHost = "";
            String remoteHost = getIpAddress(request);

            try {
                localHost = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            //判断是否需要校验
            String path = request.getRequestURI();
            String userId = "";

            context.clearContextVar();
            String sessionId = request.getSession().getId();
            MDC.put(Constant.sessionId, sessionId);
            MDC.put(Constant.traceId, traceId);
            MDC.put(Constant.userId, userId);
            MDC.put(Constant.remoteHost, remoteHost);
            MDC.put(Constant.localHost, localHost);

            context.addContextVar(Constant.traceId, traceId);
            context.addContextVar(Constant.remoteHost, remoteHost);
            context.addContextVar(Constant.localHost, localHost);
            context.addContextVar(Constant.userId, userId);

            if (StringUtils.isNotEmpty(path) && !path.endsWith("index") && !path.endsWith("index/")) {
                /**对于接口服务来说,银行编号即为用户,必须参数**/
                String institutionNo = request.getHeader(Constants.CONSTANT_HEADER_INSTITUTION);
                MDC.put(Constant.userId, institutionNo);
                context.addContextVar(Constant.userId, institutionNo);

                if (StringUtils.isEmpty(institutionNo)) {
                    throw new TransIOException(MsgRetCode.COMM_0040, Constants.CONSTANT_HEADER_INSTITUTION);
                }
                SysInfo sysInfoDomain = LocalConstants.sysInfoDomainHashMap.get(institutionNo);
                if (sysInfoDomain == null) {
                    throw new TransIOException(MsgRetCode.COMM_0044, institutionNo);
                }

                List<String> bankconf = sysInfoDomain.getSwitchmap().get(Constants.CONSTANT_REST);
                if (bankconf == null) {
                    throw new TransIOException(MsgRetCode.COMM_0042, institutionNo);
                } else if (bankconf.size() == 0) {
                    //此情况代表全量通过
                } else {
                    //获取请求接口名
                    //使用正则匹配
                    boolean cando = false;
                    for (String pattern : bankconf) {
                        if (Pattern.matches(pattern, path)) {
                            log.debug("URI{}通过准入校验{}", path, pattern);
                            cando = true;
                            break;
                        }
                    }
                    if (!cando) {
                        throw new TransIOException(MsgRetCode.COMM_0043, institutionNo, path);
                    }
                }

                /**对于接口服务来说,时间戳用于交易有效性校验,必须参数**/
                String timestampStr = request.getHeader(Constants.CONSTANT_HEADER_TIMESTAMP);
                if (StringUtils.isEmpty(timestampStr)) {
                    throw new TransIOException(MsgRetCode.COMM_0040, Constants.CONSTANT_HEADER_TIMESTAMP);
                }
                long timestamp = Long.parseLong(timestampStr);
                if (System.currentTimeMillis() - timestamp > Constants.CONSTANT_HEADER_TIMESTAMP_EXPIRE) {
                    throw new TransIOException(MsgRetCode.COMM_0041);
                }

                /**判断安全验证方式**/
                boolean checkIp = false;
                boolean checkSign = false;
                if (StringUtils.isNotEmpty(sysInfoDomain.getPublickey()) && StringUtils.isNotEmpty(sysInfoDomain.getPrivatekey())) {
                    checkSign = true;
                }
                if (StringUtils.isNotEmpty(sysInfoDomain.getCustrequestip())) {
                    checkIp = true;
                }
                if (!checkSign && !checkIp) {
                    //无任何安全设置
                    throw new TransIOException(MsgRetCode.COMM_0044, institutionNo);
                }
                if (checkIp) {
                    List<String> ips = Arrays.asList(sysInfoDomain.getCustrequestip().split(","));
                    log.info("当前请求接入IP为{}", remoteHost);
                    if (!ips.contains(remoteHost)) {
                        throw new TransIOException(MsgRetCode.COMM_0032, institutionNo);
                    }
                }

                if (checkSign) {
                    /**验证签名**/
                    String sign = request.getHeader(Constants.CONSTANT_HEADER_SIGN);
                    if (StringUtils.isEmpty(sign)) {
                        throw new TransIOException(MsgRetCode.COMM_0040, Constants.CONSTANT_HEADER_SIGN);
                    }
                    if (StringUtils.isEmpty(sysInfoDomain.getPublickey()) || StringUtils.isEmpty(sysInfoDomain.getPrivatekey())) {
                        throw new TransIOException(MsgRetCode.COMM_0046, institutionNo);
                    }
                    /**获取入参**/
                    Enumeration parameterNames = request.getParameterNames();
                    Map<String, String> params = new HashMap<>();
                    while (parameterNames.hasMoreElements()) {
                        String key = (String) parameterNames.nextElement();
                        String value = request.getParameter(key);
                        log.info("param={}:{}", key, value);
                        params.put(key, value);
                    }
                    String paramsString = createLinkString(params);
                    log.debug("获取到报文体body={}", paramsString);
                    /**开始签名计算**/
                    SysParameter sysParameter = Parameter.getSysParameter("signCheck");
                    if (sysParameter != null && Constants.FLAG_NO.equalsIgnoreCase(sysParameter.getParamValue1())) {
                        log.info("当前不校验签名");
                    } else {
                        String signData = institutionNo + timestampStr + paramsString;
                        log.debug("签名计算signData={}", signData);
                        boolean verifySign = false;
                        try {
                            verifySign = RSAManager.verify(signData.getBytes(ENCODING_UTF_8), sysInfoDomain.getPublickey(), sign);
                        } catch (Exception e) {
                            log.error("签名验证异常", e);
                            throw new TransIOException(MsgRetCode.COMM_0045);
                        }
                        if (!verifySign) {
                            throw new TransIOException(MsgRetCode.COMM_0045);
                        }
                    }
                }
            }
//             下面这句话不要动，就这样放着。你在处理你的业务逻辑之后，spring会将你的请求和响应继续往容器传或者往客户端进行传递
            boolean result = super.preHandle(request, response, handler);
            if (result) {
                flag = "S";
            }
            return result;
        } catch (Exception e) {
            JsonResult<Object> result = new JsonResult<>(false, MsgRetCode.COMM_0011);

            if (e instanceof TransIOException) {
                TransIOException ex = (TransIOException) e;
                result = new JsonResult<>(false, ex.getMsgRetCode());
                log.warn(e.getMessage());
            } else {
                log.error(e.getMessage(), e);
            }
            result.setData(null);
            return genResp(response, result);
        }
    }

    private boolean genResp(HttpServletResponse response, JsonResult<Object> result) throws IOException {

        OutputStream out = null;
        response.setCharacterEncoding(ENCODING_UTF_8);
        response.setContentType(MediaType.APPLICATION_JSON);
        try {
            out = response.getOutputStream();
            out.write(result.toString().getBytes(ENCODING_UTF_8));
            out.flush();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
//                        e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object arg2, Exception arg3)
            throws Exception {
        Long startTime = (Long) request.getAttribute(Constant.startTime);
        String localHost = MDC.get(Constant.localHost);
        String remoteHost = MDC.get(Constant.remoteHost);

        String institutionNo = request.getHeader(Constants.CONSTANT_HEADER_INSTITUTION);
        String timestamp = String.valueOf(System.currentTimeMillis());
        response.setHeader(Constants.CONSTANT_HEADER_INSTITUTION, institutionNo);
        response.setHeader(Constants.CONSTANT_HEADER_TIMESTAMP, timestamp);

        if (transLogger.isDebugEnabled()) {
            if (request != null) {
                Enumeration<String> attributeEnums = request.getAttributeNames();
                Map<String, Object> attributeMap = new HashMap<String, Object>();
                while (attributeEnums != null && attributeEnums.hasMoreElements()) {
                    String key = attributeEnums.nextElement();
                    attributeMap.put(key, request.getAttribute(key));
                }

                Enumeration<String> ParameterEnums = request.getParameterNames();
                Map<String, Object> parameterMap = new HashMap<String, Object>();
                while (ParameterEnums != null && ParameterEnums.hasMoreElements()) {
                    String key = ParameterEnums.nextElement();
                    parameterMap.put(key, request.getParameter(key));
                }
//                transLogger.debug("方法:[{}],本地IP:[{}],远端IP:[{}],入参:[Attribute:{},Parameter:{}]", request.getRequestURL(), localHost, remoteHost, attributeMap, parameterMap);
                transLogger.debug("方法:[{}],本地IP:[{}],远端IP:[{}],入参:[Parameter:{}]", request.getRequestURL(), localHost, remoteHost, parameterMap);
            }
            transLogger.debug("方法:[{}],本地IP:[{}],远端IP:[{}],入参:[{}],出参:[{}]", request.getRequestURL() + (StringUtils.isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString()), localHost, remoteHost, request, response);
        }
        monitorLogger.info("|{}|{}|{}|{}|{}|", request.getRequestURI(), localHost, remoteHost, flag, System.currentTimeMillis() - startTime);
        // 删除
        MDC.remove(Constant.sessionId);
        MDC.remove(Constant.traceId);
        MDC.remove(Constant.remoteHost);
        MDC.remove(Constant.localHost);
        MDC.remove(Constant.userId);
    }

    public static String getIpAddress(HttpServletRequest request) {
        String fromSource = "remoteip";
        String ip = request.getHeader("remoteip");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            fromSource = "X-Real-IP";
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
            fromSource = "X-Forwarded-For";
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            fromSource = "Proxy-Client-IP";
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            fromSource = "WL-Proxy-Client-IP";
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            fromSource = "HTTP_CLIENT_IP";
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            fromSource = "HTTP_X_FORWARDED_FOR";
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            fromSource = "request.getRemoteAddr";
        }
        transLogger.debug("App Client IP: " + ip + ", fromSource: " + fromSource);
        return ip;
    }


    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    private static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + StringUtils.trimToEmpty(value);
            } else {
                prestr = prestr + key + "=" + StringUtils.trimToEmpty(value) + "&";
            }
        }
        return prestr;
    }

}
