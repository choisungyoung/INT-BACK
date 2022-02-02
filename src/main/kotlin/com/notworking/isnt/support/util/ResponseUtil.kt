package com.notworking.isnt.support.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.controller.dto.ErrorResponse
import javax.servlet.http.HttpServletResponse

class ResponseUtil {

    companion object {
        fun setResponse(response: HttpServletResponse) {
            response.characterEncoding = "UTF-8"
            response.status = HttpServletResponse.SC_OK
            response.contentType = "application/json; charset=UTF-8"
            response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000")
        }

        fun setResponse(response: HttpServletResponse, errorResponse: ErrorResponse) {
            val mapper = ObjectMapper() //JSON 변경용
            setResponse(response)
            response.writer.print(mapper.writeValueAsString(errorResponse))
        }

    }
}