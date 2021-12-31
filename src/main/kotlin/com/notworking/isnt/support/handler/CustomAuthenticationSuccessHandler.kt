package com.notworking.isnt.support.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.controller.dto.ResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class CustomAuthenticationSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {
    @Override
    @Throws(ServletException::class, IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse,
        authentication: Authentication?
    ) {
        val mapper = ObjectMapper() //JSON 변경용
        val responseDTO = ResponseDTO()
        responseDTO.code = HttpStatus.OK.value()
        //val prevPage = request.session.getAttribute("prevPage").toString() //이전 페이지 가져오기
        val items: MutableMap<String, String> = HashMap()
        //items["url"] = prevPage // 이전 페이지 저장
        responseDTO.item = items
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_OK
        response.writer.print(mapper.writeValueAsString(responseDTO))
        response.writer.flush()
    }

}