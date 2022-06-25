package com.wt2024.points.dubbo.backend.filter;

import com.wt2024.points.dubbo.backend.domain.ResponseBase;
import com.wt2024.points.dubbo.backend.exception.PointsTransException;
import com.wt2024.points.dubbo.backend.retcode.MsgRetCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/8/11 14:48
 * @Project points2.0:com.wt2024.points.dubbo.backend.dubbo.filter
 */
@Activate(
        group = CommonConstants.PROVIDER,
        order = -5000
)
public class PointsProviderContextFilter extends ListenableFilter {

    public PointsProviderContextFilter() {
        super.listener = new ExceptionListener();
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    static class ExceptionListener implements Listener {

        final static String FORMAT = "%s\\{(.*?)\\}";

        final static Logger logger = LoggerFactory.getLogger(ExceptionListener.class);

        @Override
        public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {

            if (appResponse.hasException() && invocation instanceof RpcInvocation && ResponseBase.class.isAssignableFrom(((RpcInvocation) invocation).getReturnType())) {
                try {
                    Throwable exception = appResponse.getException();
                    ResponseBase response = new ResponseBase();
                    if (exception instanceof PointsTransException) {
                        response.setMsgRetCode(((PointsTransException) exception).getRetCode());
                    } else if (exception instanceof ValidationException) {
                        response = validationException(exception);
                    } else {
                        logger.warn(String.join("#", invoker.getInterface().getName(), invocation.getMethodName()), exception);
                        response = otherException(exception);
                    }

                    Class clazz = ((RpcInvocation) invocation).getReturnType();
                    if (!clazz.equals(ResponseBase.class)) {
                        Object targetObject = clazz.newInstance();
                        BeanUtils.copyProperties(response, targetObject, clazz);
                        appResponse.setValue(targetObject);
                    } else {
                        appResponse.setValue(response);
                    }
                    appResponse.setException(null);
                } catch (Exception e) {
                    logger.debug("反射处理失败，返回直接交由上层处理", e);
                }
            }

        }

        private ResponseBase validationException(Throwable exception) {
            ResponseBase response = new ResponseBase();
            String reg = String.format(FORMAT, "ConstraintViolationImpl");
            Pattern pattern = Pattern.compile(String.format(FORMAT, ConstraintViolationImpl.class.getSimpleName()));
            Matcher matcher = pattern.matcher(exception.getMessage());
            String retShow = "";
            if (matcher.find()) {
                retShow = matcher.group(1);
                logger.debug("验证结果：{}", retShow);
                Map<String, String> json = this.mapStringToMap(retShow);
                retShow = json.getOrDefault("propertyPath", "数据") + json.getOrDefault("interpolatedMessage", "校验不通过");
            }
            response.setRetcode(MsgRetCode.TRANS_0047.getRetCode());
            response.setRetmsg(StringUtils.isBlank(retShow) ? String.format(MsgRetCode.TRANS_0047.getRetShow(), "") : retShow.trim());
            return response;
        }

        private ResponseBase otherException(Throwable exception) {
            ResponseBase response = new ResponseBase(MsgRetCode.TRANS_0005);
            if (exception instanceof RuntimeException) {
                Pattern pattern = Pattern.compile(String.format(FORMAT, MsgRetCode.class.getSimpleName()));
                Matcher matcher = pattern.matcher(exception.getMessage());
                if (matcher.find()) {
                    Map<String, String> json = this.mapStringToMap(matcher.group(1));
                    response.setRetcode(json.get("retCode"));
                    response.setRetmsg(json.get("retShow"));
                }
            }
            return response;
        }

        @Override
        public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
        }

        private Map<String, String> mapStringToMap(String str) {
            String[] strs = str.replaceAll("'", "").split(",");
            Map<String, String> map = new HashMap<>();
            for (String string : strs) {
                String key = StringUtils.trimToEmpty(string.split("=")[0]);
                String value = StringUtils.trimToEmpty(string.split("=")[1]);
                map.put(key, value);
            }
            return map;
        }
    }
}
