package com.notworking.isnt.controller.developer

import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO
import com.notworking.isnt.controller.developer.dto.DeveloperSaveRequestDTO
import com.notworking.isnt.controller.developer.dto.DeveloperUpdateRequestDTO
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import kotlin.streams.toList

@RequestMapping("/api/developer")
@RestController
class DeveloperController(var developerService: DeveloperService) {

    /** 사용자 조회 */
    @GetMapping("/{email}")
    fun find(@PathVariable email: String): ResponseEntity<DeveloperFindResponseDTO> {
        var dto: DeveloperFindResponseDTO? = developerService.findDeveloperByEmail(email)?.let {
            DeveloperFindResponseDTO(
                it.email,
                it.name,
                it.introduction,
                it.pictureUrl,
                it.point,
                it.popularity
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
                    dev.email,
                    dev.name,
                    dev.introduction,
                    dev.pictureUrl,
                    dev.point,
                    dev.popularity
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
    fun update(@RequestBody dto: DeveloperUpdateRequestDTO): ResponseEntity<Void> {
        developerService.updateDeveloper(dto.toModel())

        return ResponseEntity.ok().build()
    }


    /** 사용자 삭제 */
    @DeleteMapping("/{email}")
    fun delete(@PathVariable email: String): ResponseEntity<Void> {
        developerService.deleteDeveloper(email)

        return ResponseEntity.ok().build()
    }
}