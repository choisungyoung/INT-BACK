package com.notworking.isnt.controller.common

import com.notworking.isnt.controller.common.dto.CodeFindResponseDTO
import com.notworking.isnt.controller.common.dto.InitDataFindResponseDTO
import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO
import com.notworking.isnt.service.CodeService
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/common")
@RestController
class CommonController(
    var developerService: DeveloperService,
    var codeService: CodeService
) {

    val CAREGORY_CODE = "CATEGORY"

    /** refresh token 사용자 조회 */
    @GetMapping("/refreshtoken")
    fun find(): ResponseEntity<DeveloperFindResponseDTO> {

        var user = SecurityContextHolder.getContext().authentication.principal as User?
        user ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        var dto: DeveloperFindResponseDTO? = developerService.findDeveloperByUserId(user.username)?.let {
            DeveloperFindResponseDTO(
                userId = it.userId,
                email = it.email,
                name = it.name,
                introduction = it.introduction,
                gitUrl = it.gitUrl,
                webSiteUrl = it.webSiteUrl,
                groupName = it.groupName,
                pictureUrl = it.pictureUrl,
                point = it.point,
                popularity = it.popularity
            )
        }
        //존재하지 않을 경우 에러처리
        dto ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        return ResponseEntity.ok().body(dto)
    }

    /** 코드 조회 */
    @GetMapping("/initData")
    fun findInitData(): ResponseEntity<InitDataFindResponseDTO> {

        var categoryDto: List<CodeFindResponseDTO>? = codeService.findCodeByCode(CAREGORY_CODE).map {
            CodeFindResponseDTO(
                code = it.code,
                key = it.key,
                value = it.value
            )
        }
        //존재하지 않을 경우 에러처리
        categoryDto ?: throw BusinessException(Error.CODE_NOT_FOUND)

        var dto = InitDataFindResponseDTO(
            categorys = categoryDto
        )
        return ResponseEntity.ok().body(dto)
    }
}