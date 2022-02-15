package com.notworking.isnt.support.config

import com.notworking.isnt.support.provider.JwtTokenProvider
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebMvcConfig : WebMvcConfigurer {

    override
    fun addCorsMappings(registry: CorsRegistry) {

        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .exposedHeaders(JwtTokenProvider.ACCESS_TOKEN_NAME, "*")
            .allowCredentials(true)
            //.allowedOrigins("*")
            .allowedMethods(
                HttpMethod.GET.name,
                HttpMethod.POST.name,
                HttpMethod.PUT.name,
                HttpMethod.DELETE.name,
                HttpMethod.OPTIONS.name
            )
            .maxAge(32400)


    }

}
