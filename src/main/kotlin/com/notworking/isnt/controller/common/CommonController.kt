package com.notworking.isnt.controller.common

import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO
import com.notworking.isnt.model.Developer
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/common")
@RestController
class CommonController(var developerService: DeveloperService) {

    /** 사용자 조회 */
    @GetMapping("/refreshtoken/{userId}")
    fun find(@AuthenticationPrincipal authentication: Authentication?): ResponseEntity<DeveloperFindResponseDTO> {

        authentication ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        var developer = authentication.principal as Developer

        var dto: DeveloperFindResponseDTO? = developerService.findDeveloperByUserId(developer.userId)?.let {
            DeveloperFindResponseDTO(
                userId = it.userId,
                email = it.email,
                name = it.name,
                introduction = it.introduction,
                gitUrl = it.gitUrl,
                webSiteUrl = it.webSiteUrl,
                pictureUrl = it.pictureUrl,
                point = it.point,
                popularity = it.popularity
            )
        }
        //존재하지 않을 경우 에러처리
        dto ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        return ResponseEntity.ok().body(dto)
    }
}