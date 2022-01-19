package com.notworking.isnt.support.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.controller.dto.ErrorResponse
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class CustomAuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {
    @Override
    @Throws(IOException::class, ServletException::class, BusinessException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest?, response: HttpServletResponse,
        e: AuthenticationException?
    ) {
        var error = Error.LOGIN_FAILED
        val mapper = ObjectMapper() //JSON 변경용
        val errorResponse = ErrorResponse(
            code = error.code,
            title = "업무처리 에러",
            message = error.message,
            detailMessage = e?.message.orEmpty()
        )
        response.characterEncoding = "UTF-8"
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.writer.print(mapper.writeValueAsString(errorResponse))
        response.writer.flush()
    }

}