package com.spartan.dc.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


/**
 * Swagger will help generate the document
 * 1. Configure the generated document information
 * 2. Configure the generated rules
 */

@Configuration
@Slf4j
@Profile({"dev", "test"})
public class SwaggerConfig {
    @Bean
    @Order(value = 1)
    public Docket createRestApi() {
        log.info("Go to swagger's configuration");
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.spartan.dc.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * Build the API documentation details
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // Set the page title
                .title("Spartan Dc Api")
                // Set the interface description
                .description("Spartan Dc Api")
                // Set the version
                .version("1.0")
                // Build
                .build();
    }

}


