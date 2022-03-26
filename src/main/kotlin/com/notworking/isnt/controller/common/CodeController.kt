package com.notworking.isnt.controller.common

import com.notworking.isnt.controller.common.dto.CodeFindResponseDTO
import com.notworking.isnt.service.CodeService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/code")
@RestController
class CodeController(var codeService: CodeService) {

    /** 코드 조회 */
    @GetMapping("/{code}")
    fun find(@PathVariable code: String): ResponseEntity<List<CodeFindResponseDTO>> {

        var dto: List<CodeFindResponseDTO>? = codeService.findCodeByCode(code).map {
            CodeFindResponseDTO(
                code = it.code,
                key = it.key,
                value = it.value
            )
        }
        //존재하지 않을 경우 에러처리
        dto ?: throw BusinessException(Error.CODE_NOT_FOUND)

        return ResponseEntity.ok().body(dto)
    }
}