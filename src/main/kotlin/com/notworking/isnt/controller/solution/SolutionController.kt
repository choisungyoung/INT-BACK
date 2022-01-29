package com.notworking.isnt.controller.issue

import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO
import com.notworking.isnt.controller.issue.dto.CommentFindResponseDTO
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

    var userId = "test" // TODO: Authentication 시큐리티 객체에서 받아오는 것으로 수정

    /** 솔루션 최신순 목록 조회 */
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
        var page: Page<Solution> = solutionService.findAllSolutionWithComment(pageable, issueId)
        var list: List<SolutionFindResponseDTO> = page.stream()
            .map { e ->
                SolutionFindResponseDTO(
                    e.id!!,
                    e.content,
                    e.docType.code,
                    e.recommendationCount,
                    DeveloperFindResponseDTO(
                        userId = e.developer.userId,
                        email = e.developer.email,
                        name = e.developer.name,
                        introduction = e.developer.introduction,
                        gitUrl = e.developer.gitUrl,
                        webSiteUrl = e.developer.webSiteUrl,
                        pictureUrl = e.developer.pictureUrl,
                        point = e.developer.point,
                        popularity = e.developer.popularity
                    ),
                    e.comments.stream().map {
                        CommentFindResponseDTO(
                            id = it.id!!,
                            content = it.content,
                            modifiedDate = it.getModifiedDate(),
                            developer = DeveloperFindResponseDTO(
                                userId = it.developer.userId,
                                email = it.developer.email,
                                name = it.developer.name,
                                introduction = it.developer.introduction,
                                gitUrl = it.developer.gitUrl,
                                webSiteUrl = it.developer.webSiteUrl,
                                pictureUrl = it.developer.pictureUrl,
                                point = it.developer.point,
                                popularity = it.developer.popularity
                            ),

                            )
                    }.toList(),
                    e.adoptYn,
                    e.getModifiedDate()
                )
            }.toList()

        return ResponseEntity.ok().body(PageImpl<SolutionFindResponseDTO>(list, pageable, page.totalElements))
    }

    /** 솔루션 저장 */
    @PostMapping
    fun save(@Valid @RequestBody dto: SolutionSaveRequestDTO): ResponseEntity<Void> {
        solutionService.saveSolution(dto.toModel(), userId, dto.issueId)

        return ResponseEntity.ok().build()
    }

    /** 솔루션 수정 */
    @PutMapping
    fun update(@RequestBody dto: SolutionUpdateRequestDTO): ResponseEntity<Void> {
        solutionService.updateSolution(dto.toModel())

        return ResponseEntity.ok().build()
    }

    /** 솔루션 삭제 */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        solutionService.deleteSolution(id)

        return ResponseEntity.ok().build()
    }

    /** 솔루션 추천  */
    @PutMapping("/recommend/{id}")
    fun recommend(@PathVariable id: Long): ResponseEntity<Void> {
        solutionService.recommendSolution(id, userId)
        return ResponseEntity.ok().build()
    }

    /** 솔루션 채택  */
    @PutMapping("/adopt/{id}")
    fun adopt(@PathVariable id: Long): ResponseEntity<Boolean> {
        var adoptYn = solutionService.adoptSolution(id, userId)
        return ResponseEntity.ok().body(adoptYn)
    }
}