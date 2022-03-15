package com.notworking.isnt.support.config

import com.notworking.isnt.service.GitOAuth2UserService
import com.notworking.isnt.service.UserCustomService
import com.notworking.isnt.support.filter.JwtAuthenticationFilter
import com.notworking.isnt.support.handler.CustomAuthenticationFailureHandler
import com.notworking.isnt.support.handler.CustomAuthenticationSuccessHandler
import com.notworking.isnt.support.provider.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
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

    @Autowired
    lateinit var gitOAuth2UserService: GitOAuth2UserService


    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder(11)

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.authenticationProvider(authenticationProvider())
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userCustomService)
        authProvider.setPasswordEncoder(encoder())
        return authProvider
    }


    /*@Bean
    fun corsFilter(): CorsFilter? {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        //config.addAllowedOriginPattern("*") // e.g. http://domain1.com
        config.addAllowedHeader("*")
        config.addAllowedOrigin("http://localhost:3000")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }*/
     */


    override fun configure(web: WebSecurity) {
        web.ignoring()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations())

            //.antMatchers("/")
            .antMatchers("/docs/**")
            .antMatchers("/error")
            .antMatchers("/githubLogin.html")
            .antMatchers("/login/oauth/authorize")
            //.antMatchers("/login/oauth2/code/github")
            .antMatchers("/api/auth/**")


            .antMatchers(HttpMethod.OPTIONS, "/api/**")
            .antMatchers(HttpMethod.GET, "/api/issue/**")
            .antMatchers(HttpMethod.GET, "/api/solution/**")
            .antMatchers(HttpMethod.GET, "/api/comment/**")
            .antMatchers(HttpMethod.GET, "/api/developer/**")
            .antMatchers(HttpMethod.POST, "/api/developer/**")


        //.mvcMatchers("/api/auth/login") // login이 security filter를 사용하므로 WebSecurity ignore하면 안됨
    }

    override fun configure(http: HttpSecurity?) {

        http//?.addFilter(corsFilter())
            ?.csrf()?.disable()
            ?.headers()
            ?.frameOptions()
            ?.disable()
            ?.and()
            ?.authorizeRequests()
            ?.antMatchers("/resources/**")?.permitAll()
            ?.antMatchers(HttpMethod.GET, "/api/**")?.permitAll()
            //?.anyRequest()?.permitAll()
            ?.and() // 로그아웃 기능 설정 진입점, 로그아웃 성공 시 /로 이동
            ?.addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )
            ?.oauth2Login()
            //?.redirectionEndpoint()
            //?.baseUri("/oauth2/redirect")
            //?.and()
            ?.defaultSuccessUrl("/api/auth/github/loginSuccess")
            ?.userInfoEndpoint()
            ?.userService(gitOAuth2UserService)
            ?.and()

        /*
    ?.formLogin()
    ?.usernameParameter("username")
    ?.passwordParameter("password")
    ?.permitAll()
    ?.loginPage("/login")
    ?.loginProcessingUrl("/api/auth/login")
    ?.successHandler(customAuthenticationSuccessHandler)
    ?.failureHandler(customAuthenticationFailureHandler)
    ?.and()*/

        /*
    ?.and()
    ?.logout()
    ?.logoutUrl("/api/auth/login")
    ?.addLogoutHandler()*/
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }
}