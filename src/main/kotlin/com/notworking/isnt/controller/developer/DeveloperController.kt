package com.notworking.isnt.controller.developer

import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO
import com.notworking.isnt.controller.developer.dto.DeveloperSaveRequestDTO
import com.notworking.isnt.controller.developer.dto.DeveloperUpdateRequestDTO
import com.notworking.isnt.service.DeveloperService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/developer")
@RestController
class DeveloperController(var developerService: DeveloperService) {

    /** 사용자 조회 */
    @GetMapping("/{email}")
    fun find(@PathVariable email: String): ResponseEntity<DeveloperFindResponseDTO> {
        var dto: DeveloperFindResponseDTO? = developerService.findDeveloperByEmail(email)?.let {
            DeveloperFindResponseDTO(
                it.email,
                it.password,
                it.name,
                it.introduction,
                it.pictureUrl,
                it.point,
                it.popularity
            )
        }
        return ResponseEntity.ok().body(dto)

    }

    /** 사용자 추가 */
    @PostMapping
    fun save(@RequestBody dto: DeveloperSaveRequestDTO): ResponseEntity<Void> {
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