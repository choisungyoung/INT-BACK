package com.notworking.isnt.controller.issue

import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO
import com.notworking.isnt.controller.issue.dto.*
import com.notworking.isnt.model.Issue
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.service.SolutionService
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
class IssueController(
    var issueService: IssueService,
    var solutionService: SolutionService
) {

    var email = "test@naver.com" // TODO: Authentication 시큐리티 객체에서 받아오는 것으로 수정

    /** 이슈 조회 */
    @GetMapping("/{id}")
    fun find(@PathVariable id: Long): ResponseEntity<IssueDetailFindResponseDTO> {
        var dto: IssueDetailFindResponseDTO? = issueService.findIssue(id)?.let {
            IssueDetailFindResponseDTO(
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
                it.solutions.stream().map {
                    SolutionFindResponseDTO(
                        it.id!!,
                        it.content,
                        it.docType.code,
                        it.recommendationCount,
                        DeveloperFindResponseDTO(
                            it.developer.email,
                            it.developer.name,
                            it.developer.introduction,
                            it.developer.pictureUrl,
                            it.developer.point,
                            it.developer.popularity
                        ),
                        it.comments.stream().map {
                            CommentFindResponseDTO(
                                id = it.id!!,
                                content = it.content,
                                modifiedDate = it.getModifiedDate(),
                                developer = DeveloperFindResponseDTO(
                                    it.developer.email,
                                    it.developer.name,
                                    it.developer.introduction,
                                    it.developer.pictureUrl,
                                    it.developer.point,
                                    it.developer.popularity
                                ),

                                )
                        }.toList(),
                        it.getModifiedDate()
                    )
                }.toList(),
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
        ) pageable: Pageable,
        @RequestParam(required = false) query: String?
    ): ResponseEntity<Page<IssueFindResponseDTO>> {
        var page: Page<Issue> = issueService.findAllIssueByTitleContent(pageable, query)
        var list: List<IssueFindResponseDTO> = page.stream()
            .map { issue ->
                IssueFindResponseDTO(
                    issue.id!!,
                    issue.title,
                    issue.content,
                    issue.docType.code,
                    issue.hits,
                    issue.recommendationCount,
                    solutionService.findSolutionCount(issue.id!!),  // TODO : 성능 체크하기
                    solutionService.findSolutionAdoptYn(issue.id!!),// TODO : 성능 체크하기
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

        return ResponseEntity.ok().body(PageImpl(list, pageable, page.totalElements))
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