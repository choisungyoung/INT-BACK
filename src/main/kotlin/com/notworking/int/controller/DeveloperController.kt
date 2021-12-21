package com.notworking.int.controller

import com.notworking.int.controller.dto.DeveloperDTO
import com.notworking.int.model.Developer
import com.notworking.int.service.DeveloperService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/developer")
@RestController
class DeveloperController(var developerService: DeveloperService) {

    // POST로 유저 추가
    @PostMapping
    fun put(@RequestBody dto: DeveloperDTO): ResponseEntity<DeveloperDTO> {
        developerService.saveDeveloper(dto.toModel())

        return ResponseEntity<DeveloperDTO>(HttpStatus.OK)
    }
}