package com.notworking.isnt.support.config

import org.springframework.context.annotation.Bean
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

/** 스웨거 설정 (사용안함 Rest Docs로 대체함) */
class SwaggerConfig {
    @Bean
    fun productApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.notworking.isnt"))
            .build()
            .apiInfo(this.metaInfo())
    }

    private fun metaInfo(): ApiInfo {
        return ApiInfoBuilder()
            .title("NotWorking API 문서")
            .description("NotWorking API 문서입니다.")
            .version("1.0")
            .termsOfServiceUrl("http://localhost:8080/api/doc")
            .license("Apache")
            .licenseUrl("http://license.com")
            .contact(
                Contact(
                    "ChoiSungYoung",
                    "http://notworking.com",
                    "sungyoungchoi94@gmail.com"
                )
            )
            .build()
    }
}