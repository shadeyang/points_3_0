package com.wt2024.points.restful.backend.interceptor;

import com.wt2024.base.Parameter;
import com.wt2024.base.context.ServiceContext;
import com.wt2024.base.context.Wt2024IOTransfer;
import com.wt2024.base.context.Wt2024Limit;
import com.wt2024.base.context.Wt2024Monitor;
import com.wt2024.base.entity.SysParameter;
import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.restful.backend.constant.Constants;
import com.wt2024.points.restful.backend.constant.LocalConstants;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import com.wt2024.points.restful.backend.domain.SysInfo;
import com.wt2024.points.restful.backend.filter.BodyCachingHttpServletRequestWrapper;
import com.wt2024.points.restful.backend.filter.BodyCachingHttpServletResponseWrapper;
import com.wt2024.points.restful.backend.utils.auth.RSAManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * springmvc.xml 增加web拦截器配置
 *
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/5/22 01:12
 * @Project uxun-framework:com.uxunchina.framework.web.interceptor
 */
@Component
@Slf4j
public class RestControllerInterceptor implements HandlerInterceptor {

    private static final String ENCODING_UTF_8 = StandardCharsets.UTF_8.toString();

    private String flag = Constants.FLAG_NO;

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

    private static String[] whitePathList = {"/index", "/v2/api-docs", "doc.html"};

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    @Configuration
    static class ContextPath {

        public static String restContextPath;

        @Value("${server.servlet.context-path}")
        public void setRestContextPath(String restContextPath) {
            ContextPath.restContextPath = restContextPath;
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        long startTime = System.currentTimeMillis();
        request.setAttribute(Constants.startTime, startTime);
        // 编码处理
        request.setCharacterEncoding(ENCODING_UTF_8);
        response.setCharacterEncoding(ENCODING_UTF_8);
        // * 表示允许任何域名跨域访问
        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        request.setAttribute(InputPart.DEFAULT_CHARSET_PROPERTY, ENCODING_UTF_8);

        initMDC(request);

        //判断是否需要校验
        String path = request.getRequestURI().substring(request.getContextPath().length());
        log.debug("path=={}", path);

        if (StringUtils.isNotEmpty(path) && !isWhitePath(path)) {
            /**对于接口服务来说,机构编号即为用户,必须参数**/
            String institutionNo = request.getHeader(Constants.CONSTANT_HEADER_INSTITUTION);
            SysInfo sysInfoDomain = checkInstitutionNo(institutionNo);
            try {
                checkPath(path, sysInfoDomain, institutionNo);
                if (!Constants.SWAGGER_SKIPPED_INSTITUTION_NO.equals(institutionNo)) {
                    /**对于接口服务来说,时间戳用于交易有效性校验,必须参数**/
                    String timestampStr = request.getHeader(Constants.CONSTANT_HEADER_TIMESTAMP);
                    checkTimestamp(timestampStr);
                    /**判断安全验证方式**/
                    verifySecurity(request, institutionNo, sysInfoDomain, timestampStr);
                }
            } catch (Exception e) {
                exceptionHandler(response, institutionNo, sysInfoDomain, e);
                return false;
            }
        }
        flag = Constants.FLAG_YES;
        return true;
    }

    private void exceptionHandler(HttpServletResponse response, String institutionNo, SysInfo sysInfoDomain, Exception e) throws IOException {
        ResponseResult result = new ResponseResult();
        if (e instanceof PointsException) {
            result.setCode(((PointsException) e).getPointsCode().getCode());
            result.setMessage(((PointsException) e).getPointsCode().getShow());
        } else {
            log.warn("请求前校验失败", e);
            result.setRetCode(PointsCode.COMM_0011);
        }
        response.setContentType(MediaType.APPLICATION_JSON_UTF8.toString());
        ServletOutputStream out = response.getOutputStream();
        out.write(result.toString().getBytes(ENCODING_UTF_8));
        addSign(response, sysInfoDomain, institutionNo);
        out.flush();
    }

    private void addSign(HttpServletResponse response, SysInfo sysInfoDomain, String institutionNo) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        response.setHeader(Constants.CONSTANT_HEADER_INSTITUTION, institutionNo);
        response.setHeader(Constants.CONSTANT_HEADER_TIMESTAMP, timestamp);

        if (sysInfoDomain == null || (StringUtils.isEmpty(sysInfoDomain.getPrivatekey()) && StringUtils.isEmpty(sysInfoDomain.getCustrequestip()))) {
            log.warn("不存在私钥配置，跳过签名计算");
        } else {
            if (StringUtils.isNotEmpty((sysInfoDomain.getPrivatekey()))) {
                try {
                    /**获取body**/
                    String body = "";
                    if (response instanceof BodyCachingHttpServletResponseWrapper) {
                        try {
                            body = new String(((BodyCachingHttpServletResponseWrapper) response).getBody(), ENCODING_UTF_8);
                            log.debug("获取到报文体body={}", body);
                        } catch (UnsupportedEncodingException e) {
                            log.error("获取到报文体body error", e);
                        }
                    }
                    String signData = institutionNo + timestamp + body;
                    log.debug("返回加签data={}", signData);
                    String sign = RSAManager.sign(signData.getBytes(ENCODING_UTF_8), sysInfoDomain.getPrivatekey());
                    log.debug("返回签名sign={}", sign);
                    response.setHeader(Constants.CONSTANT_HEADER_SIGN, sign);
                } catch (Exception e1) {
                    log.error("加签处理错误，跳过签名计算", e1);
                }
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object arg2, Exception arg3) {
        Long startTime = (Long) request.getAttribute(Constants.startTime);
        String localHost = MDC.get(Constants.localHost);
        String remoteHost = MDC.get(Constants.remoteHost);

        if (transLogger.isDebugEnabled()) {
            if (request != null) {
                Enumeration<String> attributeEnums = request.getAttributeNames();
                Map<String, Object> attributeMap = new HashMap<>();
                while (attributeEnums != null && attributeEnums.hasMoreElements()) {
                    String key = attributeEnums.nextElement();
                    attributeMap.put(key, request.getAttribute(key));
                }

                Enumeration<String> ParameterEnums = request.getParameterNames();
                Map<String, Object> parameterMap = new HashMap<>();
                while (ParameterEnums != null && ParameterEnums.hasMoreElements()) {
                    String key = ParameterEnums.nextElement();
                    parameterMap.put(key, request.getParameter(key));
                }
                transLogger.debug("方法:[{}],本地IP:[{}],远端IP:[{}],入参:[Parameter:{}]", request.getRequestURL(), localHost, remoteHost, parameterMap);
            }
            transLogger.debug("方法:[{}],本地IP:[{}],远端IP:[{}],入参:[{}],出参:[{}]", request.getRequestURL() + (StringUtils.isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString()), localHost, remoteHost, request, response);
        }
        monitorLogger.info("|{}|{}|{}|{}|{}|", request.getRequestURI(), localHost, remoteHost, flag, System.currentTimeMillis() - startTime);
        // 删除
        MDC.remove(Constants.sessionId);
        MDC.remove(Constants.traceId);
        MDC.remove(Constants.remoteHost);
        MDC.remove(Constants.localHost);
        MDC.remove(Constants.userId);
    }

    private void initMDC(HttpServletRequest httpRequest) {
        ServiceContext context = ServiceContext.getContext();

        String traceId = httpRequest.getHeader(Constants.traceId);
        if (StringUtils.isEmpty(traceId)) {
            traceId = UUID.randomUUID().toString().replaceAll("-", "");
        }

        String localHost = "";
        String remoteHost = getIpAddress(httpRequest);

        try {
            localHost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.debug("get host address error: {}", e.getCause());
        }

        /**对于接口服务来说,机构编号即为用户,必须参数**/
        String institutionNo = httpRequest.getHeader(Constants.CONSTANT_HEADER_INSTITUTION);

        context.clearContextVar();
        String sessionId = httpRequest.getSession().getId();
        MDC.put(Constants.sessionId, sessionId);
        MDC.put(Constants.traceId, traceId);
        MDC.put(Constants.remoteHost, remoteHost);
        MDC.put(Constants.localHost, localHost);
        MDC.put(Constants.userId, institutionNo);

        context.addContextVar(Constants.traceId, traceId);
        context.addContextVar(Constants.remoteHost, remoteHost);
        context.addContextVar(Constants.localHost, localHost);
        context.addContextVar(Constants.userId, institutionNo);
    }

    private SysInfo checkInstitutionNo(String institutionNo) {
        if (StringUtils.isEmpty(institutionNo)) {
            throw new PointsException(PointsCode.COMM_0040, Constants.CONSTANT_HEADER_INSTITUTION);
        }
        SysInfo sysInfoDomain = LocalConstants.sysInfoDomainHashMap.get(institutionNo);
        if (sysInfoDomain == null) {
            throw new PointsException(PointsCode.COMM_0044, institutionNo);
        }
        return sysInfoDomain;
    }

    private void checkPath(String path, SysInfo sysInfoDomain, String institutionNo) {
        List<String> configure = sysInfoDomain.getSwitchmap().get(Constants.CONSTANT_REST);
        if (configure == null) {
            throw new PointsException(PointsCode.COMM_0042, institutionNo);
        } else if (configure.size() == 0) {
            //此情况代表全量通过
        } else {
            //获取请求接口名
            //使用正则匹配
            boolean cando = false;
            for (String pattern : configure) {
                if (Pattern.matches(pattern, path)) {
                    log.debug("URI{}通过准入校验{}", path, pattern);
                    cando = true;
                    break;
                }
            }
            if (!cando) {
                throw new PointsException(PointsCode.COMM_0043, institutionNo, path);
            }
        }
    }

    private void checkTimestamp(String timestampStr) {
        if (StringUtils.isEmpty(timestampStr)) {
            throw new PointsException(PointsCode.COMM_0040, Constants.CONSTANT_HEADER_TIMESTAMP);
        }
        long timestamp = Long.parseLong(timestampStr);
        if (System.currentTimeMillis() - timestamp > Constants.CONSTANT_HEADER_TIMESTAMP_EXPIRE) {
            throw new PointsException(PointsCode.COMM_0041);
        }
    }

    private void verifySecurity(HttpServletRequest httpRequest, String institutionNo, SysInfo sysInfoDomain, String timestampStr) {
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
            throw new PointsException(PointsCode.COMM_0044, institutionNo);
        }
        if (checkIp) {
            List<String> ips = Arrays.asList(sysInfoDomain.getCustrequestip().split(","));
            String remoteHost = getIpAddress(httpRequest);
            log.info("当前请求接入IP为{}", remoteHost);
            if (!ips.contains(remoteHost)) {
                throw new PointsException(PointsCode.COMM_0032, institutionNo);
            }
        }

        if (checkSign) {
            /**验证签名**/
            String sign = httpRequest.getHeader(Constants.CONSTANT_HEADER_SIGN);
            if (StringUtils.isEmpty(sign)) {
                throw new PointsException(PointsCode.COMM_0040, Constants.CONSTANT_HEADER_SIGN);
            }
            if (StringUtils.isEmpty(sysInfoDomain.getPublickey()) || StringUtils.isEmpty(sysInfoDomain.getPrivatekey())) {
                throw new PointsException(PointsCode.COMM_0046, institutionNo);
            }
            /**获取入参**/
            Enumeration parameterNames = httpRequest.getParameterNames();
            Map<String, String> params = new HashMap<>();
            while (parameterNames.hasMoreElements()) {
                String key = (String) parameterNames.nextElement();
                String value = httpRequest.getParameter(key);
                log.info("param={}:{}", key, value);
                params.put(key, value);
            }
            String paramsString = createLinkString(params);
            log.debug("获取到报文参数params={}", paramsString);
            /**获取body**/
            String body = "";
            if (httpRequest instanceof BodyCachingHttpServletRequestWrapper) {
                try {
                    body = new String(((BodyCachingHttpServletRequestWrapper) httpRequest).getBody(), ENCODING_UTF_8);
                    log.debug("获取到报文体body={}", body);
                } catch (UnsupportedEncodingException e) {
                    log.error("获取到报文体body error", e);
                }
            }

            /**开始签名计算**/
            SysParameter sysParameter = Parameter.getSysParameter("signCheck");
            if (sysParameter != null && Constants.FLAG_NO.equalsIgnoreCase(sysParameter.getParamValue1())) {
                log.info("当前不校验签名");
            } else {
                String signData = institutionNo + timestampStr + paramsString + body;
                log.debug("签名计算signData={}", signData);
                boolean verifySign;
                try {
                    verifySign = RSAManager.verify(signData.getBytes(ENCODING_UTF_8), sysInfoDomain.getPublickey(), sign);
                } catch (Exception e) {
                    log.error("签名验证异常", e);
                    throw new PointsException(PointsCode.COMM_0045);
                }
                if (!verifySign) {
                    throw new PointsException(PointsCode.COMM_0045);
                }
            }
        }
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

    private boolean isWhitePath(String path) {
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        for (String white : whitePathList) {
            if (Pattern.matches(white, path)) {
                return true;
            }
        }
        return false;
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
