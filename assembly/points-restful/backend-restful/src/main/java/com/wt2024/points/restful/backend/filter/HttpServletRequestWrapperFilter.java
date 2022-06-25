package com.wt2024.points.restful.backend.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/31 17:49
 * @project points3.0:com.wt2024.points.restful.backend.filter
 */
@Component
@WebFilter(filterName = "httpServletRequestWrapperFilter", urlPatterns = {"/*"})
@Slf4j
public class HttpServletRequestWrapperFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            //遇到post方法才对request进行包装
            if (HttpMethod.POST.matches(httpRequest.getMethod())) {
                BodyCachingHttpServletRequestWrapper requestWrapper = new BodyCachingHttpServletRequestWrapper(httpRequest);
                BodyCachingHttpServletResponseWrapper responseWrapper = new BodyCachingHttpServletResponseWrapper(httpResponse);
                chain.doFilter(requestWrapper, responseWrapper);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}