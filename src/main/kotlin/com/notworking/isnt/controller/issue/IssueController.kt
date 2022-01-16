package com.notworking.isnt.controller.issue

import com.fasterxml.jackson.databind.ObjectMapper
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

    var userId = "test" // TODO: Authentication 시큐리티 객체에서 받아오는 것으로 수정

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
                it.hashtags.stream().map {
                    it.name
                }.toList(),
                DeveloperFindResponseDTO(
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
                it.solutions.stream().map {
                    SolutionFindResponseDTO(
                        it.id!!,
                        it.content,
                        it.docType.code,
                        it.recommendationCount,
                        DeveloperFindResponseDTO(
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
                        it.comments.stream().map {
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
                        it.adoptYn,
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
    ): ResponseEntity<Map<String, Object>> {
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
                    issue.hashtags.stream().map {
                        it.name
                    }.toList(),
                    DeveloperFindResponseDTO(
                        userId = issue.developer.userId,
                        email = issue.developer.email,
                        name = issue.developer.name,
                        introduction = issue.developer.introduction,
                        gitUrl = issue.developer.gitUrl,
                        webSiteUrl = issue.developer.webSiteUrl,
                        pictureUrl = issue.developer.pictureUrl,
                        point = issue.developer.point,
                        popularity = issue.developer.popularity
                    ),
                    issue.getModifiedDate()
                )
            }.toList()

        var pageImpl = PageImpl<IssueFindResponseDTO>(list, pageable, page.totalElements)

        // response에 query 추가
        var resutlMap = ObjectMapper().convertValue(pageImpl, MutableMap::class.java) as MutableMap<String, Object>
        if (query != null)
            resutlMap.put("query", query as Object) // TODO : Reflection등 이용하는 방법 찾아보기
        return ResponseEntity.ok().body(resutlMap)
    }

    /** 이슈 저장 */
    @PostMapping
    fun save(@Valid @RequestBody dto: IssueSaveRequestDTO): ResponseEntity<Void> {
        issueService.saveIssue(dto.toModel(), userId, dto.hashtags)

        return ResponseEntity.ok().build()
    }

    /** 이슈 수정 */
    @PutMapping
    fun update(@RequestBody dto: IssueUpdateRequestDTO): ResponseEntity<Void> {
        issueService.updateIssue(dto.toModel(), dto.hashtags)

        return ResponseEntity.ok().build()
    }


    /** 이슈 삭제 */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        issueService.deleteIssue(id)

        return ResponseEntity.ok().build()
    }
}