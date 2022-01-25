package com.notworking.isnt.support.filter

import com.notworking.isnt.support.provider.JwtTokenProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class JwtAuthenticationFilter(
    var jwtTokenProvider: JwtTokenProvider
) : GenericFilterBean() {

    // Request로 들어오는 Jwt Token의 유효성을 검증하는 filter를 filterChain에 등록합니다.
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(
        servletRequest: ServletRequest?,
        servletResponse: ServletResponse?,
        filterChain: FilterChain
    ) {
        val token: String? = jwtTokenProvider.resolveToken(servletRequest as HttpServletRequest)

        if (token != null && jwtTokenProvider.validateAccessToken(token)) {   // token 검증
            // TODO : getAuthentication access랑 refresh 메소드 나누거나 validateAccessToken validateRefreshToken합치거나 해야함
            val auth: Authentication = jwtTokenProvider.getAuthentication(token) // 인증 객체 생성
            SecurityContextHolder.getContext().authentication = auth // SecurityContextHolder에 인증 객체 저장
        }
        filterChain.doFilter(servletRequest, servletResponse)
    }
}