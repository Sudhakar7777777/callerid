package com.sbk.callerid;

import static com.google.common.collect.Lists.newArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig
{
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("com.sbk.callerid.controller"))
                //.paths(PathSelectors.ant("/Caller/*"))
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET,
                        newArrayList(new ResponseMessageBuilder()
                                        .code(500)
                                        .message("500 message")
                                        //.responseModel(new ModelRef("Error"))
                                        .build(),
                        new ResponseMessageBuilder()
                                .code(403)
                                .message("Forbidden!!!!!")
                                .build()));
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "CallerId REST API",
                "A standalone service that responds to requests seeking caller id information.",
                "API v1.0",
                "Terms of service: Use at your own risk, no guarantees on accuracy of data.",
                new Contact("SBK", "http://www.sbk.com", "help@sbk.com"),
                "Copyright @2018 SBK",
                "http://www.apache.org/licenses/LICENSE-2.0",
                Collections.emptyList());
        return apiInfo;
    }
}
