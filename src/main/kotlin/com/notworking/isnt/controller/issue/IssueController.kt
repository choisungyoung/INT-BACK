package com.notworking.isnt.controller.issue

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.controller.developer.dto.DeveloperFindIssueResponseDTO
import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO
import com.notworking.isnt.controller.issue.dto.*
import com.notworking.isnt.model.Issue
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.service.SolutionService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import com.querydsl.core.Tuple
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import kotlin.streams.toList


@RequestMapping("/api/issue")
@RestController
class IssueController(
    var issueService: IssueService,
    var solutionService: SolutionService,
    var developerService: DeveloperService
) {

    /** ์ด์ ์กฐํ */
    @GetMapping("/{id}")
    fun find(
        @PathVariable id: Long,
        @RequestHeader("email") email: String?
    ): ResponseEntity<IssueDetailFindResponseDTO> {

        var dto: IssueDetailFindResponseDTO? = issueService.findIssue(id)?.let {
            IssueDetailFindResponseDTO(
                id = it.id!!,
                title = it.title,
                content = it.content,
                docType = it.docType.code,
                hits = it.hits,
                recommendationCount = it.recommendationCount,
                hashtags = it.hashtags.stream().map {
                    it.name
                }.toList(),
                category = it.category,
                developer = DeveloperFindIssueResponseDTO(
                    email = it.developer.email,
                    name = it.developer.name,
                    introduction = it.developer.introduction,
                    gitUrl = it.developer.gitUrl,
                    webSiteUrl = it.developer.webSiteUrl,
                    groupName = it.developer.groupName,
                    pictureUrl = it.developer.pictureUrl,
                    point = it.developer.point,
                    popularity = it.developer.popularity,
                    followYn = developerService.existsFollowByEmail(email, it.developer.email)
                ),
                solutions = it.solutions.stream().map {
                    SolutionFindResponseDTO(
                        id = it.id!!,
                        issueId = it.issue?.id!!,
                        content = it.content,
                        docType = it.docType.code,
                        recommendationCount = it.recommendationCount,
                        developer = DeveloperFindResponseDTO(
                            email = it.developer.email,
                            name = it.developer.name,
                            introduction = it.developer.introduction,
                            gitUrl = it.developer.gitUrl,
                            webSiteUrl = it.developer.webSiteUrl,
                            groupName = it.developer.groupName,
                            pictureUrl = it.developer.pictureUrl,
                            point = it.developer.point,
                            popularity = it.developer.popularity
                        ),
                        comment = it.comments.stream().map {
                            CommentFindResponseDTO(
                                id = it.id!!,
                                content = it.content,
                                modifiedDate = it.getModifiedDate(),
                                developer = DeveloperFindResponseDTO(
                                    email = it.developer.email,
                                    name = it.developer.name,
                                    introduction = it.developer.introduction,
                                    gitUrl = it.developer.gitUrl,
                                    webSiteUrl = it.developer.webSiteUrl,
                                    groupName = it.developer.groupName,
                                    pictureUrl = it.developer.pictureUrl,
                                    point = it.developer.point,
                                    popularity = it.developer.popularity
                                ),

                                )
                        }.toList(),
                        adoptYn = it.adoptYn,
                        modifiedDate = it.getModifiedDate()
                    )
                }.toList(),
                modifiedDate = it.getModifiedDate()
            )
        }

        //์กด์ฌํ์ง ์์ ๊ฒฝ์ฐ ์๋ฌ์ฒ๋ฆฌ
        dto ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        return ResponseEntity.ok().body(dto)
    }

    /** ์ด์ ์์์?์ฅ ์กฐํ */
    @GetMapping("/temp")
    fun findIssueTemp(
        //@PathVariable("userId") pathUserId: String?,
        @RequestHeader("email") email: String?
    ): ResponseEntity<IssueTempFindResponseDTO> {

        //์กด์ฌํ์ง ์์ ๊ฒฝ์ฐ ๋น๊ฐ ๋ฆฌํด
        email ?: return ResponseEntity.ok().build()

        var dto: IssueTempFindResponseDTO? = issueService.findIssueTemp(email)?.let {
            IssueTempFindResponseDTO(
                id = it.id!!,
                title = it.title,
                content = it.content,
                docType = it.docType.code,
                category = it.category
            )
        }

        //์กด์ฌํ์ง ์์ ๊ฒฝ์ฐ ๋น๊ฐ ๋ฆฌํด
        dto ?: return ResponseEntity.ok().build()
        return ResponseEntity.ok().body(dto)
    }

    /** ์ด์ ์ต์?์ ๋ชฉ๋ก ์กฐํ */
    @GetMapping("/list")
    fun findList(
        @PageableDefault(
            size = 10,
            page = 0,
            sort = ["createdDate"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable,
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) category: String?,
    ): ResponseEntity<Map<String, Object>> {
        var page: Page<Tuple> = issueService.findAllIssue(pageable, query, category)
        var list: List<IssueFindResponseDTO> = page.map {
            var issue = it.get(0, Issue::class.java)
            var solutionCount = it.get(1, Long::class.java)
            var adoptYn: Boolean = it.get(2, Long::class.java)!! > 0

            issue ?: throw BusinessException(Error.ISSUE_NOT_FOUND)
            solutionCount = solutionCount ?: 0

            IssueFindResponseDTO(
                id = issue.id!!,
                title = issue.title,
                content = issue.content,
                docType = issue.docType.code,
                hits = issue.hits,
                recommendationCount = issue.recommendationCount,
                solutionCount = solutionCount,
                adoptYn = adoptYn,
//                hashtags = issue.hashtags.stream().map {
//                    it.name
//                }.toList(),
                category = issue.category,
                developer = DeveloperFindResponseDTO(
                    email = issue.developer.email,
                    name = issue.developer.name,
                    introduction = issue.developer.introduction,
                    gitUrl = issue.developer.gitUrl,
                    webSiteUrl = issue.developer.webSiteUrl,
                    groupName = issue.developer.groupName,
                    pictureUrl = issue.developer.pictureUrl,
                    point = issue.developer.point,
                    popularity = issue.developer.popularity
                ),
                modifiedDate = issue.getModifiedDate()
            )
        }.toList()

        var pageImpl = PageImpl(list, pageable, page.totalElements)

        // response์ query ์ถ๊ฐ
        var resutlMap = ObjectMapper().convertValue(pageImpl, MutableMap::class.java) as MutableMap<String, Object>
        if (query != null)
            resutlMap.put("query", query as Object) // TODO : Reflection๋ฑ ์ด์ฉํ๋ ๋ฐฉ๋ฒ ์ฐพ์๋ณด๊ธฐ
        return ResponseEntity.ok().body(resutlMap)
    }

    /** ์ฌ์ฉ์๋ณ ์ด์ ์ต์?์ ๋ชฉ๋ก ์กฐํ */
    @GetMapping("/list/developer/{email}")
    fun findListMyIssue(
        @PageableDefault(
            size = 10,
            page = 0,
            sort = ["createdDate"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable,
        @PathVariable("email") email: String?
    ): ResponseEntity<Page<IssueFindResponseDTO>> {

        //์กด์ฌํ์ง ์์ ๊ฒฝ์ฐ ๋น๊ฐ ๋ฆฌํด
        email ?: throw BusinessException(Error.DEVELOPER_USER_ID_HAS_NOT_HEADER)

        var page: Page<Tuple> = issueService.findAllIssueByEmail(pageable, email)
        var list: List<IssueFindResponseDTO> = page.map {
            var issue = it.get(0, Issue::class.java)
            var solutionCount = it.get(1, Long::class.java)
            var adoptYn: Boolean = it.get(2, Long::class.java)!! > 0

            issue ?: throw BusinessException(Error.ISSUE_NOT_FOUND)
            solutionCount = solutionCount ?: 0

            IssueFindResponseDTO(
                id = issue.id!!,
                title = issue.title,
                content = issue.content,
                docType = issue.docType.code,
                hits = issue.hits,
                recommendationCount = issue.recommendationCount,
                solutionCount = solutionCount,
                adoptYn = adoptYn,
//                hashtags = issue.hashtags.stream().map {
//                    it.name
//                }.toList(),
                category = issue.category,
                developer = DeveloperFindResponseDTO(
                    email = issue.developer.email,
                    name = issue.developer.name,
                    introduction = issue.developer.introduction,
                    gitUrl = issue.developer.gitUrl,
                    webSiteUrl = issue.developer.webSiteUrl,
                    groupName = issue.developer.groupName,
                    pictureUrl = issue.developer.pictureUrl,
                    point = issue.developer.point,
                    popularity = issue.developer.popularity
                ),
                modifiedDate = issue.getModifiedDate()
            )
        }.toList()

        return ResponseEntity.ok().body(PageImpl(list, pageable, page.totalElements))
    }

    /** ์ด์ ์?์ฅ */
    @PostMapping
    fun save(@Valid @RequestBody dto: IssueSaveRequestDTO): ResponseEntity<Void> {
        var user = SecurityContextHolder.getContext().authentication.principal as User?
        user ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        issueService.saveIssue(dto.toModel(), user.username, dto.hashtags)

        return ResponseEntity.ok().build()
    }

    /** ์ด์ ์์ ์?์ฅ */
    @PostMapping("temp")
    fun saveIssueTemp(@Valid @RequestBody dto: IssueTempSaveRequestDTO): ResponseEntity<Void> {
        var user = SecurityContextHolder.getContext().authentication.principal as User?
        user ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        issueService.saveIssueTemp(dto.toModel(), user.username)

        return ResponseEntity.ok().build()
    }

    /** ์ด์ ์์? */
    @PutMapping
    fun update(@RequestBody dto: IssueUpdateRequestDTO): ResponseEntity<Void> {
        issueService.updateIssue(dto.toModel(), dto.hashtags)

        return ResponseEntity.ok().build()
    }


    /** ์ด์ ์ญ์? */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        issueService.deleteIssue(id)

        return ResponseEntity.ok().build()
    }
}