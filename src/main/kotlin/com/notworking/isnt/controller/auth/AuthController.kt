package com.notworking.isnt.controller.auth

import com.notworking.isnt.controller.auth.dto.AuthChecAuthNumkResponseDTO
import com.notworking.isnt.controller.auth.dto.AuthLoginResponseDTO
import com.notworking.isnt.controller.auth.dto.AuthUpdatePasswordRequestDTO
import com.notworking.isnt.controller.issue.dto.AuthLoginRequestDTO
import com.notworking.isnt.model.Developer
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.EmailAuthService
import com.notworking.isnt.service.MailService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.provider.JwtTokenProvider
import com.notworking.isnt.support.type.Error
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RequestMapping("/api/auth")
@RestController
class AuthController(
    var developerService: DeveloperService,
    var authenticationManager: AuthenticationManager,
    var jwtTokenProvider: JwtTokenProvider,
    var mailService: MailService,
    var emailAuthService: EmailAuthService,
) {
    /** refresh token 사용자 조회 */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody dto: AuthLoginRequestDTO,
        response: HttpServletResponse
    ): ResponseEntity<AuthLoginResponseDTO> {

        val authentication = authenticationManager
            .authenticate(UsernamePasswordAuthenticationToken(dto.email, dto.password))
        authentication ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        var email = (authentication.principal as Developer).email
        var developerDto = developerService.findDeveloperByEmail(email)?.let {
            AuthLoginResponseDTO(
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


    /** 패스워드 변경 인증메일 발송 */
    @GetMapping("/sendFindPasswordMail/{email}")
    fun sendFindPasswordAuthMail(@PathVariable email: String): ResponseEntity<Void> {
        mailService.sendFindPasswordMail(email);
        return ResponseEntity.ok().build()
    }

    /** 회원가입 인증메일 발송 */
    @GetMapping("/sendSignUpMail/{email}")
    fun sendSignUpAuthMail(@PathVariable email: String): ResponseEntity<Void> {
        mailService.sendSignUpMail(email);
        return ResponseEntity.ok().build()
    }

    /** 인증번호 확인 */
    @GetMapping("/checkAuthNum/{email}")
    fun checkAuthNum(
        @PathVariable email: String,
        @RequestParam authNum: Int
    ): ResponseEntity<AuthChecAuthNumkResponseDTO> {
        var successYn = emailAuthService.checkAuthNum(email, authNum)
        return ResponseEntity.ok().body(
            AuthChecAuthNumkResponseDTO(
                successYn = successYn
            )
        )
    }


    /** 사용자 패스워드 수정 */
    @PutMapping("/password")
    fun updatePassword(@RequestBody dto: AuthUpdatePasswordRequestDTO): ResponseEntity<Void> {

        var successYn = emailAuthService.recheckAuthNum(dto.email, dto.authNum)
        if (!successYn) {   // 인증번호 재확인
            throw BusinessException(Error.AUTH_INVALID_AUTH_NUM)
        }

        developerService.updatePasswordDeveloper(dto.email, dto.password)

        return ResponseEntity.ok().build()
    }
}