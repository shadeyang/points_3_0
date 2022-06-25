package com.wt2024.points.restful.backend.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wt2024.points.restful.backend.interceptor.RestControllerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/30 17:48
 * @project points3.0:com.wt2024.points.restful.backend.config
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RestControllerInterceptor restControllerInterceptor;

    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(UTF_8);
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(responseBodyConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restControllerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/index", "/error")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/doc.html", "/**/favicon.ico", "/v2/api-docs");
    }

}
