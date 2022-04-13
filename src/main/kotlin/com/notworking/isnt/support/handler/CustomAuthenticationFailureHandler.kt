package com.notworking.isnt.support.handler

import com.notworking.isnt.controller.dto.ErrorResponse
import com.notworking.isnt.support.type.Error
import com.notworking.isnt.support.util.ResponseUtil
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


//@Component
class CustomAuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {
    @Override
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest?, response: HttpServletResponse,
        e: AuthenticationException?
    ) {
        var error = Error.AUTH_FAILED
        val errorResponse = ErrorResponse(
            code = error.code,
            title = "업무처리 에러",
            message = error.message,
            detailMessage = e?.message.orEmpty()
        )
        ResponseUtil.setResponse(response, errorResponse)
        response.writer.flush()
    }

}