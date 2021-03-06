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

    private val beforeSaveIssueEmail = "beforeSaveIssueEmail@naver.com"
    private var beforeSaveIssueId: Long = 0
    private var beforeSaveSolutionId: Long = 0
    private val notFindIssueId: Long = -999

    private val saveDto = IssueSaveRequestDTO(
        title = "Test Title",
        content = "test content",
        docType = DocType.TEXT.code,
        hashtags = mutableListOf("save test"),
        category = "BACK-END"
    )

    private val saveIssueTempDto = IssueTempSaveRequestDTO(
        title = "Temp Test Title",
        content = "temp test content",
        docType = DocType.TEXT.code,
        category = "BACK-END"
    )

    private val updateDto = IssueUpdateRequestDTO(
        id = 0,     // beforeEach?????? ?????????
        title = "Update Test Title",
        content = "update test content",
        docType = DocType.MARK_DOWN.code,
        mutableListOf("update test"),
        category = "BACK-END"
    )

    @BeforeEach
    fun beforeEach() {

        developerService.saveDeveloper(
            Developer(
                id = null,
                email = beforeSaveIssueEmail,
                pwd = "aa12345^",
                name = "issueTester",
                introduction = "???????????????",
                gitUrl = "test git url",
                webSiteUrl = "test web site url",
                pictureUrl = "before testUrl",
                groupName = "test group",
                point = 0,
                popularity = 0,
            )
        )

        // ????????? ?????? ??????
        beforeSaveIssueId = issueService.saveIssue(
            Issue(
                id = null,
                title = "Before Test Title",
                content = "Before Test content",
                docType = DocType.TEXT,
                category = "BACK-END"
            ),
            beforeSaveIssueEmail,
            mutableListOf("before test", "1312", "12123213")
        ).id!!

        beforeSaveSolutionId = solutionService.saveSolution(
            Solution(
                id = null,
                content = "before test solution",
                docType = DocType.TEXT
            ),
            beforeSaveIssueEmail,
            beforeSaveIssueId
        ).id!!

        commentService.saveComment(
            Comment(
                id = null,
                content = "test comment"
            ),
            beforeSaveIssueEmail,
            beforeSaveSolutionId
        )

        // ?????? ?????????????????? id ??????
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
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveIssueEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "save-issue",
                    requestFields(
                        fieldWithPath("title").description("?????? ??????"),
                        fieldWithPath("content").description("?????? ??????"),
                        fieldWithPath("docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("hashtags.[]").description("???????????? ?????????"),
                        fieldWithPath("category").description("????????????"),
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
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveIssueEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "save-issue-temp",
                    requestFields(
                        fieldWithPath("title").description("?????? ??????"),
                        fieldWithPath("content").description("?????? ??????"),
                        fieldWithPath("docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("category").description("????????????"),
                    )
                )

            )


        mockMvc.perform(
            RestDocumentationRequestBuilders.get("$uri/temp")
                .header("email", beforeSaveIssueEmail)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-issue-temp",
                    responseFields(
                        fieldWithPath("id").description("????????????"),
                        fieldWithPath("title").description("??????"),
                        fieldWithPath("content").description("??????"),
                        fieldWithPath("docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("category").description("????????????"),
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
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveIssueEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun testFindList() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/list")
                .param("page", "0")
                .param("size", "5")
                .param("query", "Test")
                .param("category", "BACK-END")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-issue-list",
                    requestParameters(
                        parameterWithName("page").description("?????? ?????????"),
                        parameterWithName("size").description("?????? ????????? ?????????"),
                        parameterWithName("query").description("?????????"),
                        parameterWithName("category").description("??????")
                    ),
                    responseFields(
                        fieldWithPath("content.[].id").description("????????????"),
                        fieldWithPath("content.[].title").description("??????"),
                        fieldWithPath("content.[].content").description("??????"),
                        fieldWithPath("content.[].docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("content.[].hits").description("?????????"),
                        fieldWithPath("content.[].recommendationCount").description("?????????"),
                        fieldWithPath("content.[].solutionCount").description("????????? ???"),
                        fieldWithPath("content.[].adoptYn").description("????????????"),
                        //fieldWithPath("content.[].hashtags.[]").description("???????????? ?????????"),
                        fieldWithPath("content.[].category").description("????????????"),
                        fieldWithPath("content.[].developer.email").description("????????? ?????????"),
                        fieldWithPath("content.[].developer.name").description("????????? ??????"),
                        fieldWithPath("content.[].developer.introduction").description("????????? ??????"),
                        fieldWithPath("content.[].developer.gitUrl").description("????????? ?????????"),
                        fieldWithPath("content.[].developer.webSiteUrl").description("????????? ????????????(?????????) ??????"),
                        fieldWithPath("content.[].developer.groupName").description("????????? ??????"),
                        fieldWithPath("content.[].developer.pictureUrl").description("????????? ????????????"),
                        fieldWithPath("content.[].developer.point").description("????????? ??????"),
                        fieldWithPath("content.[].developer.popularity").description("????????? ?????????"),
                        fieldWithPath("content.[].modifiedDate").description("??????????????????"),
                        fieldWithPath("pageable.sort.unsorted").description("????????????"),
                        fieldWithPath("pageable.sort.sorted").description("????????????"),
                        fieldWithPath("pageable.sort.empty").description("????????????"),
                        fieldWithPath("pageable.pageNumber").description("????????????"),
                        fieldWithPath("pageable.pageSize").description("???????????????"),
                        fieldWithPath("pageable.offset").description("?????????"),
                        fieldWithPath("pageable.unpaged").description("??????????????? ???????????????"),
                        fieldWithPath("pageable.paged").description("??????????????? ????????????"),
                        fieldWithPath("totalPages").description("??? ????????? ???"),
                        fieldWithPath("totalElements").description("??? ?????? ???"),
                        fieldWithPath("last").description("????????? ??????"),
                        fieldWithPath("numberOfElements").description("?????? ???"),
                        fieldWithPath("first").description("??? ??????"),
                        fieldWithPath("sort.unsorted").description("????????????"),
                        fieldWithPath("sort.sorted").description("????????????"),
                        fieldWithPath("sort.empty").description("??????????????????"),
                        fieldWithPath("size").description("??????"),
                        fieldWithPath("number").description("??????"),
                        fieldWithPath("empty").description("????????????"),
                        fieldWithPath("query").description("?????????"),
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
                    docType = DocType.TEXT,
                    category = "BACK-END"
                ),
                beforeSaveIssueEmail,
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
                    "find-issue-list-pagenation",
                    responseFields(
                        fieldWithPath("content.[].id").description("????????????"),
                        fieldWithPath("content.[].title").description("??????"),
                        fieldWithPath("content.[].content").description("??????"),
                        fieldWithPath("content.[].docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("content.[].hits").description("?????????"),
                        fieldWithPath("content.[].recommendationCount").description("?????????"),
                        fieldWithPath("content.[].solutionCount").description("????????? ???"),
                        fieldWithPath("content.[].adoptYn").description("????????????"),
                        //fieldWithPath("content.[].hashtags.[]").description("???????????? ?????????"),
                        fieldWithPath("content.[].category").description("????????????"),
                        fieldWithPath("content.[].developer.email").description("????????? ?????????"),
                        fieldWithPath("content.[].developer.name").description("????????? ??????"),
                        fieldWithPath("content.[].developer.introduction").description("????????? ??????"),
                        fieldWithPath("content.[].developer.pictureUrl").description("????????? ????????????"),
                        fieldWithPath("content.[].developer.point").description("????????? ??????"),
                        fieldWithPath("content.[].developer.popularity").description("????????? ?????????"),
                        fieldWithPath("content.[].modifiedDate").description("??????????????????"),
                        fieldWithPath("pageable").description("pageable"),
                        fieldWithPath("totalPages").description("??? ????????? ???"),
                        fieldWithPath("totalElements").description("??? ?????? ???"),
                        fieldWithPath("last").description("????????? ??????"),
                        fieldWithPath("numberOfElements").description("?????? ???"),
                        fieldWithPath("first").description("??? ??????"),
                        fieldWithPath("sort.unsorted").description("????????????"),
                        fieldWithPath("sort.sorted").description("????????????"),
                        fieldWithPath("sort.empty").description("??????????????????"),
                        fieldWithPath("size").description("??????"),
                        fieldWithPath("number").description("??????"),
                        fieldWithPath("empty").description("????????????"),
                    )
                )
            )
    }

    @Test
    fun testValidFindList() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("$uri/list")
                .param("page", "2")
                .param("size", "5")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun testFindListByEmail() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/list/developer/$beforeSaveIssueEmail")
                .param("page", "0")
                .param("size", "5")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-issue-list-myIssue",
                    requestParameters(
                        parameterWithName("page").description("?????? ?????????"),
                        parameterWithName("size").description("?????? ????????? ?????????"),
                    ),
                    responseFields(
                        fieldWithPath("content.[].id").description("????????????"),
                        fieldWithPath("content.[].title").description("??????"),
                        fieldWithPath("content.[].content").description("??????"),
                        fieldWithPath("content.[].docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("content.[].hits").description("?????????"),
                        fieldWithPath("content.[].recommendationCount").description("?????????"),
                        fieldWithPath("content.[].solutionCount").description("????????? ???"),
                        fieldWithPath("content.[].adoptYn").description("????????????"),
                        //fieldWithPath("content.[].hashtags.[]").description("???????????? ?????????"),
                        fieldWithPath("content.[].category").description("????????????"),
                        fieldWithPath("content.[].developer.email").description("????????? ?????????"),
                        fieldWithPath("content.[].developer.name").description("????????? ??????"),
                        fieldWithPath("content.[].developer.introduction").description("????????? ??????"),
                        fieldWithPath("content.[].developer.gitUrl").description("????????? ?????????"),
                        fieldWithPath("content.[].developer.webSiteUrl").description("????????? ????????????(?????????) ??????"),
                        fieldWithPath("content.[].developer.groupName").description("????????? ??????"),
                        fieldWithPath("content.[].developer.pictureUrl").description("????????? ????????????"),
                        fieldWithPath("content.[].developer.point").description("????????? ??????"),
                        fieldWithPath("content.[].developer.popularity").description("????????? ?????????"),
                        fieldWithPath("content.[].modifiedDate").description("??????????????????"),
                        fieldWithPath("pageable.sort.unsorted").description("????????????"),
                        fieldWithPath("pageable.sort.sorted").description("????????????"),
                        fieldWithPath("pageable.sort.empty").description("????????????"),
                        fieldWithPath("pageable.pageNumber").description("????????????"),
                        fieldWithPath("pageable.pageSize").description("???????????????"),
                        fieldWithPath("pageable.offset").description("?????????"),
                        fieldWithPath("pageable.unpaged").description("??????????????? ???????????????"),
                        fieldWithPath("pageable.paged").description("??????????????? ????????????"),
                        fieldWithPath("totalPages").description("??? ????????? ???"),
                        fieldWithPath("totalElements").description("??? ?????? ???"),
                        fieldWithPath("last").description("????????? ??????"),
                        fieldWithPath("numberOfElements").description("?????? ???"),
                        fieldWithPath("first").description("??? ??????"),
                        fieldWithPath("sort.unsorted").description("????????????"),
                        fieldWithPath("sort.sorted").description("????????????"),
                        fieldWithPath("sort.empty").description("??????????????????"),
                        fieldWithPath("size").description("??????"),
                        fieldWithPath("number").description("??????"),
                        fieldWithPath("empty").description("????????????"),
                    )
                )
            )
    }

    @Test
    fun testFind() {

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("$uri/{id}", beforeSaveIssueId)
                .header("email", beforeSaveIssueEmail)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-issue",
                    pathParameters(
                        parameterWithName("id").description("????????????")
                    ),
                    responseFields(
                        fieldWithPath("id").description("????????????"),
                        fieldWithPath("title").description("??????"),
                        fieldWithPath("content").description("??????"),
                        fieldWithPath("docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("hits").description("?????????"),
                        fieldWithPath("recommendationCount").description("?????????"),
                        fieldWithPath("hashtags.[]").description("???????????? ?????????"),
                        fieldWithPath("category").description("????????????"),
                        fieldWithPath("developer.email").description("????????? ?????????"),
                        fieldWithPath("developer.name").description("????????? ??????"),
                        fieldWithPath("developer.introduction").description("????????? ??????"),
                        fieldWithPath("developer.gitUrl").description("????????? ?????????"),
                        fieldWithPath("developer.webSiteUrl").description("????????? ????????????(?????????) ??????"),
                        fieldWithPath("developer.groupName").description("????????? ??????"),
                        fieldWithPath("developer.pictureUrl").description("????????? ????????????"),
                        fieldWithPath("developer.point").description("????????? ??????"),
                        fieldWithPath("developer.popularity").description("????????? ?????????"),
                        fieldWithPath("developer.followYn").description("????????? ????????? ??????"),
                        fieldWithPath("modifiedDate").description("??????????????????"),
                        fieldWithPath("solutions.[].id").description("????????????"),
                        fieldWithPath("solutions.[].issueId").description("?????? ?????????"),
                        fieldWithPath("solutions.[].content").description("??????"),
                        fieldWithPath("solutions.[].docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("solutions.[].recommendationCount").description("?????????"),
                        fieldWithPath("solutions.[].adoptYn").description("????????????"),
                        fieldWithPath("solutions.[].developer.email").description("????????? ?????????"),
                        fieldWithPath("solutions.[].developer.name").description("????????? ??????"),
                        fieldWithPath("solutions.[].developer.introduction").description("????????? ??????"),
                        fieldWithPath("solutions.[].developer.gitUrl").description("????????? ?????????"),
                        fieldWithPath("solutions.[].developer.webSiteUrl").description("????????? ????????????(?????????) ??????"),
                        fieldWithPath("solutions.[].developer.groupName").description("????????? ??????"),
                        fieldWithPath("solutions.[].developer.pictureUrl").description("????????? ????????????"),
                        fieldWithPath("solutions.[].developer.point").description("????????? ??????"),
                        fieldWithPath("solutions.[].developer.popularity").description("????????? ?????????"),
                        fieldWithPath("solutions.[].comment.[].id").description("????????? ?????? ?????????"),
                        fieldWithPath("solutions.[].comment.[].content").description("????????? ??????"),
                        fieldWithPath("solutions.[].comment.[].modifiedDate").description("????????? ??????????????????"),
                        fieldWithPath("solutions.[].comment.[].developer.email").description("????????? ????????? ?????????"),
                        fieldWithPath("solutions.[].comment.[].developer.name").description("????????? ????????? ??????"),
                        fieldWithPath("solutions.[].comment.[].developer.introduction").description("????????? ????????? ??????"),
                        fieldWithPath("solutions.[].comment.[].developer.gitUrl").description("????????? ????????? ?????????"),
                        fieldWithPath("solutions.[].comment.[].developer.webSiteUrl").description("????????? ????????? ????????????(?????????) ??????"),
                        fieldWithPath("solutions.[].comment.[].developer.groupName").description("????????? ????????? ??????"),
                        fieldWithPath("solutions.[].comment.[].developer.pictureUrl").description("????????? ????????? ????????????"),
                        fieldWithPath("solutions.[].comment.[].developer.point").description("????????? ????????? ?????????"),
                        fieldWithPath("solutions.[].comment.[].developer.popularity").description("????????? ????????? ?????????"),
                        fieldWithPath("solutions.[].modifiedDate").description("??????????????????"),
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
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveIssueEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "update-issue",
                    requestFields(
                        fieldWithPath("id").description("????????????"),
                        fieldWithPath("title").description("?????? ??????"),
                        fieldWithPath("content").description("?????? ??????"),
                        fieldWithPath("docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("hashtags.[]").description("???????????? ?????????"),
                        fieldWithPath("category").description("????????????"),
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
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveIssueEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "delete-issue",
                    pathParameters(
                        parameterWithName("id").description("????????????")
                    ),
                )
            )
    }
}
