package com.notworking.isnt.controller.auth

import com.notworking.isnt.controller.auth.dto.AuthLoginResponseDTO
import com.notworking.isnt.controller.issue.dto.AuthLoginRequestDTO
import com.notworking.isnt.model.Developer
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.provider.JwtTokenProvider
import com.notworking.isnt.support.type.Error
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RequestMapping("/api/auth")
@RestController
class AuthController(
    var developerService: DeveloperService,
    var authenticationManager: AuthenticationManager,
    var jwtTokenProvider: JwtTokenProvider
) {
    /** refresh token 사용자 조회 */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody dto: AuthLoginRequestDTO,
        response: HttpServletResponse
    ): ResponseEntity<AuthLoginResponseDTO> {

        val authentication = authenticationManager
            .authenticate(UsernamePasswordAuthenticationToken(dto.username, dto.password))
        authentication ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        var userId = (authentication.principal as Developer).userId
        var developerDto = developerService.findDeveloperByUserId(userId)?.let {
            AuthLoginResponseDTO(
                userId = it.userId,
                email = it.email,
                name = it.name,
                introduction = it.introduction,
                gitUrl = it.gitUrl,
                webSiteUrl = it.webSiteUrl,
                groupName = it.groupName,
                pictureUrl = it.pictureUrl,
                point = it.point,
                popularity = it.popularity,
            )
        }

        response.addHeader(JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(authentication))
        response.addCookie(
            Cookie(
                JwtTokenProvider.REFRESH_TOKEN_NAME,
                jwtTokenProvider.buildRefreshToken(authentication)
            )
        )

        return ResponseEntity.ok().body(developerDto)
    }
}