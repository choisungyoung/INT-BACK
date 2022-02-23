package com.notworking.isnt.controller.auth

import com.notworking.isnt.model.Developer
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.provider.JwtTokenProvider
import com.notworking.isnt.support.type.Error
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession


@RequestMapping("/api/auth/github")
@Controller
class AuthGitController(
    var developerService: DeveloperService,
    var authenticationManager: AuthenticationManager,
    var jwtTokenProvider: JwtTokenProvider
) {
    /** refresh token 사용자 조회 */
    @GetMapping("/loginSuccess")
    fun loginGit(httpSession: HttpSession, httpServletResponse: HttpServletResponse): String {

        httpSession.getAttribute("user") ?: throw BusinessException(Error.AUTH_FAILED)
        var developer = httpSession.getAttribute("user") as Developer
        httpSession.invalidate()
        return "redirect:http://notworking.kr/git/success?token=${jwtTokenProvider.buildAccessToken(developer.userId)}"
    }
}