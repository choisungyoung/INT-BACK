package com.notworking.isnt.controller.issue

import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO
import com.notworking.isnt.controller.issue.dto.IssueFindResponseDTO
import com.notworking.isnt.controller.issue.dto.IssueSaveRequestDTO
import com.notworking.isnt.controller.issue.dto.IssueUpdateRequestDTO
import com.notworking.isnt.model.Issue
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import kotlin.streams.toList

@RequestMapping("/api/issue")
@RestController
class IssueController(var issueService: IssueService) {

    var email = "test@naver.com" // TODO: Authentication 시큐리티 객체에서 받아오는 것으로 수정

    /** 이슈 조회 */
    @GetMapping("/{id}")
    fun find(@PathVariable id: Long): ResponseEntity<IssueFindResponseDTO> {
        var dto: IssueFindResponseDTO? = issueService.findIssue(id)?.let {
            IssueFindResponseDTO(
                it.id!!,
                it.title,
                it.content,
                it.docType.code,
                it.hits,
                it.recommendationCount,
                DeveloperFindResponseDTO(
                    it.developer.email,
                    it.developer.name,
                    it.developer.introduction,
                    it.developer.pictureUrl,
                    it.developer.point,
                    it.developer.popularity
                ),
                it.getModifiedDate()
            )
        }

        //존재하지 않을 경우 에러처리
        dto ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        return ResponseEntity.ok().body(dto)
    }

    /** 이슈 최신순 목록 조회 */
    @GetMapping("/list/latest")
    fun findList(
        @PageableDefault(
            size = 10,
            page = 0,
            sort = ["createdDate"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Page<IssueFindResponseDTO>> {
        var page: Page<Issue> = issueService.findAllIssue(pageable)
        var list: List<IssueFindResponseDTO> = page.stream()
            .map { issue ->
                IssueFindResponseDTO(
                    issue.id!!,
                    issue.title,
                    issue.content,
                    issue.docType.code,
                    issue.hits,
                    issue.recommendationCount,
                    DeveloperFindResponseDTO(
                        issue.developer.email,
                        issue.developer.name,
                        issue.developer.introduction,
                        issue.developer.pictureUrl,
                        issue.developer.point,
                        issue.developer.popularity
                    ),
                    issue.getModifiedDate()
                )
            }.toList()

        return ResponseEntity.ok().body(PageImpl<IssueFindResponseDTO>(list, pageable, page.totalElements))
    }

    /** 이슈 저장 */
    @PostMapping
    fun save(@Valid @RequestBody dto: IssueSaveRequestDTO): ResponseEntity<Void> {
        issueService.saveIssue(dto.toModel(), email)

        return ResponseEntity.ok().build()
    }

    /** 이슈 수정 */
    @PutMapping
    fun update(@RequestBody dto: IssueUpdateRequestDTO): ResponseEntity<Void> {
        issueService.updateIssue(dto.toModel())

        return ResponseEntity.ok().build()
    }


    /** 이슈 삭제 */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        issueService.deleteIssue(id)

        return ResponseEntity.ok().build()
    }
}