package com.mg.smartrent.user.config.documentation;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Log4j2
public class DocumentationConfig {

    private Environment environment;

    public DocumentationConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public Docket apiDocket() {
        log.info("Initializing Swagger API documentation...");
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/rest/**"))
                .build()
                .apiInfo(apiInfo());
    }

    @Bean
    public ApiInfo apiInfo() {
        final ApiInfoBuilder builder = new ApiInfoBuilder();
        builder.title(environment.getProperty("spring.application.name"))
                .description("User Service")
                .version("1.0")
                .termsOfServiceUrl("http://toBeDone.com")
                .contact(new Contact("Andrei Maimas", "http://toBeDone", "maimas.andrei@gmail.com"))
                .license("(C) Copyright SmartRent")
                .licenseUrl("http://toBeDone.com");

        return builder.build();
    }
}
