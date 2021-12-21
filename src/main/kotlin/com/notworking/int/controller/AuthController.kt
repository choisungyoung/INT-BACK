package com.notworking.int.controller

import com.notworking.int.controller.dto.LoginDTO
import com.notworking.int.model.Developer
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RequestMapping("/api/auth")
@RestController
class AuthController {

    // POST로 유저 추가
    @GetMapping("/login")
    fun login(): ResponseEntity<LoginDTO> {
        logger.debug(">>> Login")

        return ResponseEntity<LoginDTO>(HttpStatus.OK)
    }
    @RequestMapping("/loginSuccess")
    fun loginSuccess(): ResponseEntity<LoginDTO> {
        logger.debug(">>> Login")

        return ResponseEntity<LoginDTO>(HttpStatus.OK)
    }
}