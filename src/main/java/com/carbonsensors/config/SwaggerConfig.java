package com.carbonsensors.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.carbonsensors.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Carbon Sensors RESTFul API",
                "API for carbon sensors",
                "API 1.0.0",
                "https://www.apache.org/licenses/LICENSE-2.0",
                new Contact("Fernandes", "https://github.com/eduardo-fernandes/carbon-sensors", "fernandeseduardo@protonmail.com"),
                "Apache License, version 2.0", "https://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());
    }
}
