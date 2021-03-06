package com.notworking.isnt.support.filter

import com.notworking.isnt.controller.dto.ErrorResponse
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.provider.JwtTokenProvider
import com.notworking.isnt.support.type.Error
import com.notworking.isnt.support.util.ResponseUtil
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(
    var jwtTokenProvider: JwtTokenProvider

) : GenericFilterBean() {
    var whiteList: List<String> = listOf(
        "/",
        "/api/auth/loginSuccess/github",
    )

    // Request로 들어오는 Jwt Token의 유효성을 검증하는 filter를 filterChain에 등록합니다.
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(
        servletRequest: ServletRequest?,
        servletResponse: ServletResponse?,
        filterChain: FilterChain
    ) {
        val token: String? = jwtTokenProvider.resolveToken(servletRequest as HttpServletRequest)

        try {
            if (token == null) {
                if (whiteList.contains(servletRequest.requestURI)) {
                    logger.debug("whiteList url : $servletRequest.requestURI")
                } else {
                    throw BusinessException(Error.AUTH_NOT_FOUND_TOKEN)
                }
            } else {
                if (jwtTokenProvider.validateAccessToken(token)) {   // token 검증
                    // TODO : getAuthentication access랑 refresh 메소드 나누거나 validateAccessToken validateRefreshToken합치거나 해야함
                    val auth: Authentication = jwtTokenProvider.getAuthentication(token) // 인증 객체 생성
                    SecurityContextHolder.getContext().authentication = auth // SecurityContextHolder에 인증 객체 저장
                } else {
                    throw IllegalArgumentException()
                }
            }
        } catch (e: Exception) {
            var error: Error? = null

            error = if (e is BusinessException) {
                e.error
            } else {
                Error.AUTH_INVALID_TOKEN
            }

            val errorResponse = ErrorResponse(
                code = error.code,
                title = "토큰 에러",
                message = error.message,
                detailMessage = e?.message.orEmpty()
            )
            ResponseUtil.setResponse(response = servletResponse as HttpServletResponse, errorResponse)
            servletResponse.status = HttpStatus.UNAUTHORIZED.value()
            return
        }

        filterChain.doFilter(servletRequest, servletResponse)
    }
}