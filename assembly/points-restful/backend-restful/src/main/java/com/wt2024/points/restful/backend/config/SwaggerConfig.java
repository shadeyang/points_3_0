package com.wt2024.points.restful.backend.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.wt2024.points.restful.backend.constant.Constants.*;

/**
 * @ClassName SwaggerConfig
 * @Description: TODO
 * @Author shade.yang
 * @Date 2020/5/22
 * @Version V1.0
 **/
@Configuration
@EnableSwagger2WebMvc
@EnableSwagger2WebFlux
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${info.app.name}")
    private String appName;
    @Value("${info.app.version}")
    private String appVersion;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("SpringMVC Rest")
                .apiInfo(apiInfo())
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(this.getParameters());
//                .securityContexts(Arrays.asList(securityContexts()))
//                .securitySchemes(Arrays.asList(securitySchemes()));
    }

    private List<Parameter> getParameters() {
        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder institutionPar = new ParameterBuilder();
        institutionPar.name(CONSTANT_HEADER_INSTITUTION).defaultValue(SWAGGER_SKIPPED_INSTITUTION_NO).description("Header通讯协议机构").modelRef(new ModelRef("string")).parameterType("header").required(true).build();
        pars.add(institutionPar.build());
        ParameterBuilder timestampPar = new ParameterBuilder();
        timestampPar.name(CONSTANT_HEADER_TIMESTAMP).description("Header时间戳（ms）(swagger测试专用机构跳过验证)").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(timestampPar.build());
        ParameterBuilder signPar = new ParameterBuilder();
        signPar.name(CONSTANT_HEADER_SIGN).description("Header数据签名 (swagger测试专用机构跳过验证)").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(signPar.build());
        return pars;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Points Swagger 接口")
                .description("Points Swagger 接口描述")
                .version(appVersion).termsOfServiceUrl("https://api.wt2024.cn/")
                        .contact(new Contact("Shade.Yang","https://www.wt2024.com/","shade.yang@wt2024.com"))
//                        .license("The Apache License")
                .licenseUrl("https://api.wt2024.cn/")
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    private SecurityScheme securitySchemes() {
        return new ApiKey("Authorization", "Authorization", "header");
    }

    private SecurityContext securityContexts() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("xxx", "描述信息");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Authorization", authorizationScopes));
    }

    @Component
    @Primary
    class DocumentationConfig implements SwaggerResourcesProvider {

        @Override
        public List<SwaggerResource> get() {
            List resources = new ArrayList<>();
            resources.add(swaggerResource("SpringMVC Rest", "/v2/api-docs", "2.0"));
            return resources;
        }

        private SwaggerResource swaggerResource(String name, String location, String version) {
            SwaggerResource swaggerResource = new SwaggerResource();
            swaggerResource.setName(name);
            swaggerResource.setLocation(location);
            swaggerResource.setSwaggerVersion(version);
            return swaggerResource;
        }
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}
