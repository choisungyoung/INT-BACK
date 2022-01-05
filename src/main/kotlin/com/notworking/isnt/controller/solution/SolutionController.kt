package com.notworking.isnt.controller.issue

import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO
import com.notworking.isnt.controller.issue.dto.SolutionFindResponseDTO
import com.notworking.isnt.controller.issue.dto.SolutionSaveRequestDTO
import com.notworking.isnt.controller.issue.dto.SolutionUpdateRequestDTO
import com.notworking.isnt.model.Solution
import com.notworking.isnt.service.SolutionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import kotlin.streams.toList

@RequestMapping("/api/solution")
@RestController
class SolutionController(var solutionService: SolutionService) {

    var email = "test@naver.com" // TODO: Authentication 시큐리티 객체에서 받아오는 것으로 수정

    /** 이슈 최신순 목록 조회 */
    @GetMapping("/list/{issueId}")
    fun findList(
        @PageableDefault(
            size = 10,
            page = 0,
            sort = ["createdDate"],
            direction = Sort.Direction.ASC
        ) pageable: Pageable,
        @PathVariable issueId: Long
    ): ResponseEntity<Page<SolutionFindResponseDTO>> {
        var page: Page<Solution> = solutionService.findAllSolution(pageable, issueId)
        var list: List<SolutionFindResponseDTO> = page.stream()
            .map { e ->
                SolutionFindResponseDTO(
                    e.id!!,
                    e.content,
                    e.docType.code,
                    e.recommendationCount,
                    DeveloperFindResponseDTO(
                        e.developer.email,
                        e.developer.name,
                        e.developer.introduction,
                        e.developer.pictureUrl,
                        e.developer.point,
                        e.developer.popularity
                    ),
                    e.getModifiedDate()
                )
            }.toList()

        return ResponseEntity.ok().body(PageImpl<SolutionFindResponseDTO>(list, pageable, page.totalElements))
    }

    /** 이슈 저장 */
    @PostMapping
    fun save(@Valid @RequestBody dto: SolutionSaveRequestDTO): ResponseEntity<Void> {
        solutionService.saveSolution(dto.toModel(), email, dto.issueId)

        return ResponseEntity.ok().build()
    }

    /** 이슈 수정 */
    @PutMapping
    fun update(@RequestBody dto: SolutionUpdateRequestDTO): ResponseEntity<Void> {
        solutionService.updateSolution(dto.toModel())

        return ResponseEntity.ok().build()
    }


    /** 이슈 삭제 */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        solutionService.deleteSolution(id)

        return ResponseEntity.ok().build()
    }
}