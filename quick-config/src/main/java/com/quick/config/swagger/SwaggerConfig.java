package com.quick.config.swagger;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger配置
 *
 * @author Liujinxin
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfig {

    /**
     * 安全认证分组
     */
    @Bean
    public Docket docketBaseAuth() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("安全认证")
                        .description("认证服务API")
                        .termsOfServiceUrl("")
                        .version("1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.quick.auth.controller"))
                // 正则匹配请求路径，并分配至当前分组，当前所有接口
                .paths(PathSelectors.any())
                .build()
                // 分组名称
                .groupName("安全认证")
                .globalOperationParameters(null);
    }

    /**
     * 日志服务分组
     */
    @Bean
    public Docket docketBaseLog() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("日志服务")
                        .description("日志服务API")
                        .termsOfServiceUrl("")
                        .version("1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.quick.log.controller"))
                // 正则匹配请求路径，并分配至当前分组，当前所有接口
                .paths(PathSelectors.any())
                .build()
                // 分组名称
                .groupName("日志服务")
                .globalOperationParameters(null);
    }

    /**
     * 基础服务分组 基本
     */
    @Bean
    public Docket docketBaseBase() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("基础服务")
                        .description("基础服务API")
                        .termsOfServiceUrl("")
                        .version("1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.quick.base.controller"))
                // 正则匹配请求路径，并分配至当前分组，当前所有接口
                .paths(PathSelectors.any())
                .build()
                // 分组名称
                .groupName("基础服务")
                .globalOperationParameters(null);
    }

}

