package com.notworking.isnt.support.config

import com.notworking.isnt.service.UserCustomService
import com.notworking.isnt.support.filter.JwtAuthenticationFilter
import com.notworking.isnt.support.handler.CustomAuthenticationFailureHandler
import com.notworking.isnt.support.handler.CustomAuthenticationSuccessHandler
import com.notworking.isnt.support.provider.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configurable
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var userCustomService: UserCustomService

    @Autowired
    lateinit var customAuthenticationSuccessHandler: CustomAuthenticationSuccessHandler

    @Autowired
    lateinit var customAuthenticationFailureHandler: CustomAuthenticationFailureHandler

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder(11)
    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.authenticationProvider(authenticationProvider())
    }

    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userCustomService)
        authProvider.setPasswordEncoder(encoder())
        return authProvider
    }

    override fun configure(http: HttpSecurity?) {

        http?.csrf()?.disable()
            ?.headers()
            ?.frameOptions()
            ?.disable()
            ?.and()
            ?.authorizeRequests()
            ?.antMatchers("/resources/**")?.permitAll()
            ?.anyRequest()?.permitAll()
            ?.and()
            ?.formLogin()
            ?.usernameParameter("username")
            ?.passwordParameter("password")
            ?.permitAll()
            ?.loginPage("/login")
            ?.loginProcessingUrl("/api/auth/login")
            ?.successHandler(customAuthenticationSuccessHandler)
            ?.failureHandler(customAuthenticationFailureHandler)
            ?.and()
            ?.addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

        /*
    ?.and()
    ?.logout()
    ?.logoutUrl("/api/auth/login")
    ?.addLogoutHandler()*/
    }


}