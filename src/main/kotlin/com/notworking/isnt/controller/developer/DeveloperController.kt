package com.notworking.isnt.controller.developer

import com.notworking.isnt.controller.developer.dto.*
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import kotlin.streams.toList

@RequestMapping("/api/developer")
@RestController
class DeveloperController(
    var developerService: DeveloperService,
) {

    /** 사용자 조회 */
    @GetMapping("/{name}")
    fun find(@PathVariable name: String): ResponseEntity<DeveloperFindDetailResponseDTO> {
        var dto: DeveloperFindDetailResponseDTO? = developerService.findDeveloperByName(name)?.let {
            DeveloperFindDetailResponseDTO(
                email = it.email,
                name = it.name,
                introduction = it.introduction,
                gitUrl = it.gitUrl,
                webSiteUrl = it.webSiteUrl,
                groupName = it.groupName,
                pictureUrl = it.pictureUrl,
                point = it.point,
                popularity = it.popularity,
                followers = developerService.findFollowersByEmail(it.email)
            )
        }
        //존재하지 않을 경우 에러처리
        dto ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        return ResponseEntity.ok().body(dto)
    }

    /** 사용자 목록 */
    @GetMapping("/list")
    fun findDeveloperList(): ResponseEntity<List<DeveloperFindResponseDTO>> {
        var list: List<DeveloperFindResponseDTO>? = developerService.findAllDeveloper()?.stream()
            .map { dev ->
                DeveloperFindResponseDTO(
                    email = dev.email,
                    name = dev.name,
                    introduction = dev.introduction,
                    gitUrl = dev.gitUrl,
                    webSiteUrl = dev.webSiteUrl,
                    groupName = dev.groupName,
                    pictureUrl = dev.pictureUrl,
                    point = dev.point,
                    popularity = dev.popularity
                )
            }.toList()

        //존재하지 않을 경우 에러처리
        list ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        return ResponseEntity.ok().body(list)
    }

    /** 사용자 추가 */
    @PostMapping
    fun save(@Valid @RequestBody dto: DeveloperSaveRequestDTO): ResponseEntity<Void> {
        developerService.saveDeveloper(dto.toModel())

        return ResponseEntity.ok().build()
    }

    /** 사용자 수정 */
    @PutMapping
    fun update(@RequestBody dto: DeveloperUpdateRequestDTO): ResponseEntity<DeveloperFindResponseDTO> {

        var user = SecurityContextHolder.getContext().authentication.principal as User?
        user ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        if (user.username != dto.email) {
            throw BusinessException(Error.DEVELOPER_MODIFY_SELF_ONLY)
        }

        var dto: DeveloperFindResponseDTO? = developerService.updateDeveloper(dto.toModel())?.let {
            DeveloperFindResponseDTO(
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

        return ResponseEntity.ok().body(dto)
    }

    /** 사용자 삭제 */
    @DeleteMapping("/{email}")
    fun delete(@PathVariable email: String): ResponseEntity<Void> {

        var user = SecurityContextHolder.getContext().authentication.principal as User?
        user ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        if (user.username != email) {
            throw BusinessException(Error.DEVELOPER_MODIFY_SELF_ONLY)
        }
        developerService.deleteDeveloper(email)

        return ResponseEntity.ok().build()
    }

    /** 사용자 닉네임 중복체크 */
    @GetMapping("/checkName/{name}")
    fun checkName(@PathVariable name: String): ResponseEntity<DeveloperCheckResponseDTO> {
        return ResponseEntity.ok()
            .body(DeveloperCheckResponseDTO(duplicateYn = developerService.existsDeveloperByName(name)))
    }

    /** 사용자 아이디 중복체크 */
    @GetMapping("/checkEmail/{email}")
    fun checkEmail(@PathVariable email: String): ResponseEntity<DeveloperCheckResponseDTO> {
        return ResponseEntity.ok()
            .body(DeveloperCheckResponseDTO(duplicateYn = developerService.existsDeveloperByEmail(email)))
    }


    /** 사용자 팔로우 */
    @PutMapping("/follow/{email}")
    fun follow(@PathVariable email: String): ResponseEntity<Void> {

        var user = SecurityContextHolder.getContext().authentication.principal as User?
        user ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        developerService.followDeveloper(user.username, email)

        return ResponseEntity.ok().build()
    }


}