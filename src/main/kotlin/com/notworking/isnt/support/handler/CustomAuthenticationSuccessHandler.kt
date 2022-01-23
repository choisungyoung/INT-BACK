package com.notworking.isnt.support.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO
import com.notworking.isnt.model.Developer
import com.notworking.isnt.repository.DeveloperRepository
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class CustomAuthenticationSuccessHandler(
    val environment: Environment,
    var developerRepository: DeveloperRepository
) : SimpleUrlAuthenticationSuccessHandler() {
    @Override
    @Throws(ServletException::class, IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse,
        authentication: Authentication?
    ) {
        val mapper = ObjectMapper() //JSON 변경용

        authentication ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        var userId = (authentication.principal as Developer).userId
        var developerDto = developerRepository.findByUserId(userId)?.let {
            DeveloperFindResponseDTO(
                userId = it.userId,
                email = it.email,
                name = it.name,
                introduction = it.introduction,
                gitUrl = it.gitUrl,
                webSiteUrl = it.webSiteUrl,
                pictureUrl = it.pictureUrl,
                point = it.point,
                popularity = it.popularity,
            )
        }
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_OK
        response.writer.print(mapper.writeValueAsString(developerDto))
        response.contentType = "application/json; charset=UTF-8"
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000")

        // TODO: 로그인 응답 전체적으로 수정 필요..

        var expirationTime: Long = environment.getProperty("token.expiration-time", "30").toLong();
        developerDto ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        var token = Jwts.builder()
            .setSubject(developerDto.userId)
            .setExpiration(
                Date.from(
                    LocalDateTime.now().plusMinutes(expirationTime).atZone(ZoneId.of("Asia/Seoul")).toInstant()
                )
            )
            .signWith(SignatureAlgorithm.HS512, environment.getProperty("token.secret"))
            .compact()
        response.addHeader("Access-Token", token)
        response.writer.flush()

    }

}