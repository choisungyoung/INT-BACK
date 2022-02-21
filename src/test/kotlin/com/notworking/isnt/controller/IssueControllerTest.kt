package com.notworking.isnt.controller

import com.notworking.isnt.CommonMvcTest
import com.notworking.isnt.controller.issue.dto.IssueSaveRequestDTO
import com.notworking.isnt.controller.issue.dto.IssueTempSaveRequestDTO
import com.notworking.isnt.controller.issue.dto.IssueUpdateRequestDTO
import com.notworking.isnt.model.Comment
import com.notworking.isnt.model.Developer
import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.Solution
import com.notworking.isnt.repository.HashtagRepository
import com.notworking.isnt.service.CommentService
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.service.SolutionService
import com.notworking.isnt.support.provider.JwtTokenProvider
import com.notworking.isnt.support.type.DocType
import mu.KotlinLogging
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

private val log = KotlinLogging.logger {}

class IssueControllerTest(
    @Autowired var developerService: DeveloperService,
    @Autowired var issueService: IssueService,
    @Autowired var solutionService: SolutionService,
    @Autowired var commentService: CommentService,
    @Autowired var hashtagRepository: HashtagRepository,
) : CommonMvcTest() {
    private var uri: String = "/api/issue"

    private val beforeSaveIssueUserId = "issueTester"
    private var beforeSaveIssueId: Long = 0
    private var beforeSaveSolutionId: Long = 0
    private val notFindIssueId: Long = -999

    private val saveDto = IssueSaveRequestDTO(
        title = "Test Title",
        content = "test content",
        docType = DocType.TEXT.code,
        mutableListOf("save test")
    )

    private val saveIssueTempDto = IssueTempSaveRequestDTO(
        title = "Temp Test Title",
        content = "temp test content",
        docType = DocType.TEXT.code,
    )

    private val updateDto = IssueUpdateRequestDTO(
        id = 0,     // beforeEach에서 재설정
        title = "Update Test Title",
        content = "update test content",
        docType = DocType.MARK_DOWN.code,
        mutableListOf("update test")
    )

    @BeforeEach
    fun beforeEach() {

        developerService.saveDeveloper(
            Developer(
                id = null,
                userId = beforeSaveIssueUserId,
                email = "beforeSaveIssueEmail@naver.com",
                pwd = "aa12345^",
                name = "issueTester",
                introduction = "안녕하세요",
                gitUrl = "test git url",
                webSiteUrl = "test web site url",
                pictureUrl = "before testUrl",
                groupName = "test group",
                point = 0,
                popularity = 0,
            )
        )

        // 테스트 이슈 추가
        beforeSaveIssueId = issueService.saveIssue(
            Issue(
                id = null,
                title = "Before Test Title",
                content = "Before Test content",
                docType = DocType.TEXT
            ),
            beforeSaveIssueUserId,
            mutableListOf("before test")
        ).id!!

        beforeSaveSolutionId = solutionService.saveSolution(
            Solution(
                id = null,
                content = "before test solution",
                docType = DocType.TEXT
            ),
            beforeSaveIssueUserId,
            beforeSaveIssueId
        ).id!!

        commentService.saveComment(
            Comment(
                id = null,
                content = "test comment"
            ),
            beforeSaveIssueUserId,
            beforeSaveSolutionId
        )

        // 수정 테스트케이스 id 설정
        updateDto.id = beforeSaveIssueId;

        log.debug("=========Before Each========")
        var developerList: List<Developer> = developerService.findAllDeveloper()
        log.debug("======developer=====")
        for (developer in developerList) {
            log.debug(developer.toString())
        }

        var issueList: List<Issue> = issueService.findAllIssue()
        log.debug("======issue=====")
        for (issue in issueList) {
            log.debug(issue.toString())
        }
    }

    @AfterEach
    fun printAllList() {

        log.debug("=========AfterEach========")
        var issueList: List<Issue> = issueService.findAllIssue();

        for (issue in issueList) {
            log.debug(issue.toString())
        }
    }

    @Test
    fun testSave() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .content(mapper.writeValueAsString(saveDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveIssueUserId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "save-issue",
                    requestFields(
                        fieldWithPath("title").description("이슈 제목"),
                        fieldWithPath("content").description("이슈 내용"),
                        fieldWithPath("docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("hashtags.[]").description("해시태그 리스트"),
                    )
                )

            )
    }

    @Test
    fun testSaveIssueTemp() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("$uri/temp")
                .content(mapper.writeValueAsString(saveIssueTempDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveIssueUserId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "save-issue-temp",
                    requestFields(
                        fieldWithPath("title").description("이슈 제목"),
                        fieldWithPath("content").description("이슈 내용"),
                        fieldWithPath("docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                    )
                )

            )


        mockMvc.perform(
            RestDocumentationRequestBuilders.get("$uri/temp")
                .header("userId", beforeSaveIssueUserId)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-issue-temp",
                    responseFields(
                        fieldWithPath("id").description("고유번호"),
                        fieldWithPath("title").description("제목"),
                        fieldWithPath("content").description("내용"),
                        fieldWithPath("docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                    )
                )

            )
    }

    @Test
    fun testSaveValidation() {
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .content("{\"title\":\"\",\"content\":\"test content\",\"docType\":\"TEXT\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveIssueUserId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun testFindList() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/list/latest")
                .param("page", "0")
                .param("size", "5")
                .param("query", "test")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-issue-list-latest",
                    requestParameters(
                        parameterWithName("page").description("조회 페이지"),
                        parameterWithName("size").description("조회 페이지 사이즈"),
                        parameterWithName("query").description("검색어")
                    ),
                    responseFields(
                        fieldWithPath("content.[].id").description("고유번호"),
                        fieldWithPath("content.[].title").description("제목"),
                        fieldWithPath("content.[].content").description("내용"),
                        fieldWithPath("content.[].docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("content.[].hits").description("조회수"),
                        fieldWithPath("content.[].recommendationCount").description("추천수"),
                        fieldWithPath("content.[].solutionCount").description("솔루션 수"),
                        fieldWithPath("content.[].adoptYn").description("채택여부"),
                        fieldWithPath("content.[].hashtags.[]").description("해시태그 리스트"),
                        fieldWithPath("content.[].developer.userId").description("작성자 아이디"),
                        fieldWithPath("content.[].developer.email").description("작성자 이메일"),
                        fieldWithPath("content.[].developer.name").description("작성자 이름"),
                        fieldWithPath("content.[].developer.introduction").description("작성자 소개"),
                        fieldWithPath("content.[].developer.gitUrl").description("작성자 깃주소"),
                        fieldWithPath("content.[].developer.webSiteUrl").description("작성자 웹사이트(블로그) 주소"),
                        fieldWithPath("content.[].developer.groupName").description("작성자 소속"),
                        fieldWithPath("content.[].developer.pictureUrl").description("작성자 사진경로"),
                        fieldWithPath("content.[].developer.point").description("작성자 점수"),
                        fieldWithPath("content.[].developer.popularity").description("작성자 인기도"),
                        fieldWithPath("content.[].modifiedDate").description("최종수정일시"),
                        fieldWithPath("pageable.sort.unsorted").description("정렬종류"),
                        fieldWithPath("pageable.sort.sorted").description("정렬종류"),
                        fieldWithPath("pageable.sort.empty").description("정렬종류"),
                        fieldWithPath("pageable.pageNumber").description("페이지수"),
                        fieldWithPath("pageable.pageSize").description("페이지크기"),
                        fieldWithPath("pageable.offset").description("오프셋"),
                        fieldWithPath("pageable.unpaged").description("페이지정보 불포함여부"),
                        fieldWithPath("pageable.paged").description("페이지정보 포함여부"),
                        fieldWithPath("totalPages").description("총 페이지 수"),
                        fieldWithPath("totalElements").description("총 요소 수"),
                        fieldWithPath("last").description("마지막 여부"),
                        fieldWithPath("numberOfElements").description("요소 수"),
                        fieldWithPath("first").description("첫 여부"),
                        fieldWithPath("sort.unsorted").description("정렬여부"),
                        fieldWithPath("sort.sorted").description("정렬여부"),
                        fieldWithPath("sort.empty").description("정렬존재여부"),
                        fieldWithPath("size").description("크기"),
                        fieldWithPath("number").description("번째"),
                        fieldWithPath("empty").description("존재여부"),
                        fieldWithPath("query").description("검색어"),
                    )
                )
            )
    }

    @Disabled
    @Test
    fun testPagenationFindList() {

        for (i: Int in 1..24)
            issueService.saveIssue(
                Issue(
                    id = null,
                    title = "Pagenation Test Title" + i,
                    content = "Pagenation Test content" + i,
                    docType = DocType.TEXT
                ),
                beforeSaveIssueUserId,
                mutableListOf("test")
            )
        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/list/latest")
                .param("page", "2")
                .param("size", "5")
                .param("query", "test")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-issue-list-latest-pagenation",
                    responseFields(
                        fieldWithPath("content.[].id").description("고유번호"),
                        fieldWithPath("content.[].title").description("제목"),
                        fieldWithPath("content.[].content").description("내용"),
                        fieldWithPath("content.[].docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("content.[].hits").description("조회수"),
                        fieldWithPath("content.[].recommendationCount").description("추천수"),
                        fieldWithPath("content.[].solutionCount").description("솔루션 수"),
                        fieldWithPath("content.[].adoptYn").description("채택여부"),
                        fieldWithPath("content.[].hashtags.[]").description("해시태그 리스트"),
                        fieldWithPath("content.[].developer.email").description("작성자 이메일"),
                        fieldWithPath("content.[].developer.name").description("작성자 이름"),
                        fieldWithPath("content.[].developer.introduction").description("작성자 소개"),
                        fieldWithPath("content.[].developer.pictureUrl").description("작성자 사진경로"),
                        fieldWithPath("content.[].developer.point").description("작성자 점수"),
                        fieldWithPath("content.[].developer.popularity").description("작성자 인기도"),
                        fieldWithPath("content.[].modifiedDate").description("최종수정일시"),
                        fieldWithPath("pageable").description("pageable"),
                        fieldWithPath("totalPages").description("총 페이지 수"),
                        fieldWithPath("totalElements").description("총 요소 수"),
                        fieldWithPath("last").description("마지막 여부"),
                        fieldWithPath("numberOfElements").description("요소 수"),
                        fieldWithPath("first").description("첫 여부"),
                        fieldWithPath("sort.unsorted").description("정렬여부"),
                        fieldWithPath("sort.sorted").description("정렬여부"),
                        fieldWithPath("sort.empty").description("정렬존재여부"),
                        fieldWithPath("size").description("크기"),
                        fieldWithPath("number").description("번째"),
                        fieldWithPath("empty").description("존재여부"),
                    )
                )
            )
    }

    @Test
    fun testValidFindList() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("$uri/list/latest")
                .param("page", "2")
                .param("size", "5")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun testFindListMyIssue() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/list/myIssue")
                .param("page", "0")
                .param("size", "5")
                .header("userId", beforeSaveIssueUserId)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-issue-list-myIssue",
                    requestParameters(
                        parameterWithName("page").description("조회 페이지"),
                        parameterWithName("size").description("조회 페이지 사이즈"),
                    ),
                    responseFields(
                        fieldWithPath("content.[].id").description("고유번호"),
                        fieldWithPath("content.[].title").description("제목"),
                        fieldWithPath("content.[].content").description("내용"),
                        fieldWithPath("content.[].docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("content.[].hits").description("조회수"),
                        fieldWithPath("content.[].recommendationCount").description("추천수"),
                        fieldWithPath("content.[].solutionCount").description("솔루션 수"),
                        fieldWithPath("content.[].adoptYn").description("채택여부"),
                        fieldWithPath("content.[].hashtags.[]").description("해시태그 리스트"),
                        fieldWithPath("content.[].developer.userId").description("작성자 아이디"),
                        fieldWithPath("content.[].developer.email").description("작성자 이메일"),
                        fieldWithPath("content.[].developer.name").description("작성자 이름"),
                        fieldWithPath("content.[].developer.introduction").description("작성자 소개"),
                        fieldWithPath("content.[].developer.gitUrl").description("작성자 깃주소"),
                        fieldWithPath("content.[].developer.webSiteUrl").description("작성자 웹사이트(블로그) 주소"),
                        fieldWithPath("content.[].developer.groupName").description("작성자 소속"),
                        fieldWithPath("content.[].developer.pictureUrl").description("작성자 사진경로"),
                        fieldWithPath("content.[].developer.point").description("작성자 점수"),
                        fieldWithPath("content.[].developer.popularity").description("작성자 인기도"),
                        fieldWithPath("content.[].modifiedDate").description("최종수정일시"),
                        fieldWithPath("pageable.sort.unsorted").description("정렬종류"),
                        fieldWithPath("pageable.sort.sorted").description("정렬종류"),
                        fieldWithPath("pageable.sort.empty").description("정렬종류"),
                        fieldWithPath("pageable.pageNumber").description("페이지수"),
                        fieldWithPath("pageable.pageSize").description("페이지크기"),
                        fieldWithPath("pageable.offset").description("오프셋"),
                        fieldWithPath("pageable.unpaged").description("페이지정보 불포함여부"),
                        fieldWithPath("pageable.paged").description("페이지정보 포함여부"),
                        fieldWithPath("totalPages").description("총 페이지 수"),
                        fieldWithPath("totalElements").description("총 요소 수"),
                        fieldWithPath("last").description("마지막 여부"),
                        fieldWithPath("numberOfElements").description("요소 수"),
                        fieldWithPath("first").description("첫 여부"),
                        fieldWithPath("sort.unsorted").description("정렬여부"),
                        fieldWithPath("sort.sorted").description("정렬여부"),
                        fieldWithPath("sort.empty").description("정렬존재여부"),
                        fieldWithPath("size").description("크기"),
                        fieldWithPath("number").description("번째"),
                        fieldWithPath("empty").description("존재여부"),
                    )
                )
            )
    }

    @Test
    fun testFind() {

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("$uri/{id}", beforeSaveIssueId)
                .header("userId", "tjddud")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-issue",
                    pathParameters(
                        parameterWithName("id").description("고유번호")
                    ),
                    responseFields(
                        fieldWithPath("id").description("고유번호"),
                        fieldWithPath("title").description("제목"),
                        fieldWithPath("content").description("내용"),
                        fieldWithPath("docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("hits").description("조회수"),
                        fieldWithPath("recommendationCount").description("추천수"),
                        fieldWithPath("hashtags.[]").description("해시태그 리스트"),
                        fieldWithPath("developer.userId").description("작성자 아이디"),
                        fieldWithPath("developer.email").description("작성자 이메일"),
                        fieldWithPath("developer.name").description("작성자 이름"),
                        fieldWithPath("developer.introduction").description("작성자 소개"),
                        fieldWithPath("developer.gitUrl").description("작성자 깃주소"),
                        fieldWithPath("developer.webSiteUrl").description("작성자 웹사이트(블로그) 주소"),
                        fieldWithPath("developer.groupName").description("작성자 소속"),
                        fieldWithPath("developer.pictureUrl").description("작성자 사진경로"),
                        fieldWithPath("developer.point").description("작성자 점수"),
                        fieldWithPath("developer.popularity").description("작성자 인기도"),
                        fieldWithPath("developer.followYn").description("작성자 팔로우 여부"),
                        fieldWithPath("modifiedDate").description("최종수정일시"),
                        fieldWithPath("solutions.[].id").description("고유번호"),
                        fieldWithPath("solutions.[].content").description("내용"),
                        fieldWithPath("solutions.[].docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("solutions.[].recommendationCount").description("추천수"),
                        fieldWithPath("solutions.[].adoptYn").description("채택여부"),
                        fieldWithPath("solutions.[].developer.userId").description("작성자 아이디"),
                        fieldWithPath("solutions.[].developer.email").description("작성자 이메일"),
                        fieldWithPath("solutions.[].developer.name").description("작성자 이름"),
                        fieldWithPath("solutions.[].developer.introduction").description("작성자 소개"),
                        fieldWithPath("solutions.[].developer.gitUrl").description("작성자 깃주소"),
                        fieldWithPath("solutions.[].developer.webSiteUrl").description("작성자 웹사이트(블로그) 주소"),
                        fieldWithPath("solutions.[].developer.groupName").description("작성자 소속"),
                        fieldWithPath("solutions.[].developer.pictureUrl").description("작성자 사진경로"),
                        fieldWithPath("solutions.[].developer.point").description("작성자 점수"),
                        fieldWithPath("solutions.[].developer.popularity").description("작성자 인기도"),
                        fieldWithPath("solutions.[].comment.[].id").description("코멘트 고유 아이디"),
                        fieldWithPath("solutions.[].comment.[].content").description("코멘트 내용"),
                        fieldWithPath("solutions.[].comment.[].modifiedDate").description("코멘트 최종수정일시"),
                        fieldWithPath("solutions.[].comment.[].developer.userId").description("코멘트 작성자 아이디"),
                        fieldWithPath("solutions.[].comment.[].developer.email").description("코멘트 작성자 이메일"),
                        fieldWithPath("solutions.[].comment.[].developer.name").description("코멘트 작성자 이름"),
                        fieldWithPath("solutions.[].comment.[].developer.introduction").description("코멘트 작성자 소개"),
                        fieldWithPath("solutions.[].comment.[].developer.gitUrl").description("코멘트 작성자 깃주소"),
                        fieldWithPath("solutions.[].comment.[].developer.webSiteUrl").description("코멘트 작성자 웹사이트(블로그) 주소"),
                        fieldWithPath("solutions.[].comment.[].developer.groupName").description("코멘트 작성자 소속"),
                        fieldWithPath("solutions.[].comment.[].developer.pictureUrl").description("코멘트 작성자 사진경로"),
                        fieldWithPath("solutions.[].comment.[].developer.point").description("코멘트 작성자 포인트"),
                        fieldWithPath("solutions.[].comment.[].developer.popularity").description("코멘트 작성자 인기도"),
                        fieldWithPath("solutions.[].modifiedDate").description("최종수정일시"),
                    )
                )
            )
    }

    @Test
    fun testNotFind() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("$uri/{id}", notFindIssueId)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun testUpdate() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.put(uri)
                .content(mapper.writeValueAsString(updateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveIssueUserId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "update-issue",
                    requestFields(
                        fieldWithPath("id").description("고유번호"),
                        fieldWithPath("title").description("이슈 제목"),
                        fieldWithPath("content").description("이슈 내용"),
                        fieldWithPath("docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("hashtags.[]").description("해시태그 리스트"),
                    )
                )
            )
    }

    @Test
    fun testDelete() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .delete("$uri/{id}", beforeSaveIssueId)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveIssueUserId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "delete-issue",
                    pathParameters(
                        parameterWithName("id").description("고유번호")
                    ),
                )
            )
    }
}
