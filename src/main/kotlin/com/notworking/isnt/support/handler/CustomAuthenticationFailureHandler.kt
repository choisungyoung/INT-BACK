package com.notworking.isnt.support.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.controller.dto.ResponseDTO
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
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest?, response: HttpServletResponse,
        exception: AuthenticationException?
    ) {
        val mapper = ObjectMapper() //JSON 변경용
        val responseDTO = ResponseDTO()
        responseDTO.code = HttpStatus.UNAUTHORIZED.value()
        responseDTO.message = "아이디 혹은 비밀번호가 일치하지 않습니다."
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_OK
        response.writer.print(mapper.writeValueAsString(responseDTO))
        response.writer.flush()
    }

}