package com.notworking.isnt.controller

import com.notworking.isnt.CommonMvcTest
import com.notworking.isnt.controller.issue.dto.SolutionSaveRequestDTO
import com.notworking.isnt.controller.issue.dto.SolutionUpdateRequestDTO
import com.notworking.isnt.model.Comment
import com.notworking.isnt.model.Developer
import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.Solution
import com.notworking.isnt.service.CommentService
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.service.SolutionService
import com.notworking.isnt.support.provider.JwtTokenProvider
import com.notworking.isnt.support.type.DocType
import mu.KotlinLogging
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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

class SolutionControllerTest(
    @Autowired var developerService: DeveloperService,
    @Autowired var issueService: IssueService,
    @Autowired var solutionService: SolutionService,
    @Autowired var commentService: CommentService,
) : CommonMvcTest() {
    private var uri: String = "/api/solution"

    private val beforeSaveSolutionEmail = "beforeSaveSolutionEmail@naver.com"
    private var beforeSaveSolutionId: Long = 0
    private var beforeSaveIssueId: Long = 0

    private val saveDto = SolutionSaveRequestDTO(
        content = "test content",
        docType = DocType.TEXT.code,
        issueId = 0 // beforeEach?????? ?????????
    )

    private val updateDto = SolutionUpdateRequestDTO(
        id = 0,     // beforeEach?????? ?????????
        content = "update test content",
        docType = DocType.MARK_DOWN.code
    )

    @BeforeEach
    fun beforeEach() {

        developerService.saveDeveloper(
            Developer(
                id = null,
                email = beforeSaveSolutionEmail,
                pwd = "aa12345^",
                name = "solutionTester",
                introduction = "???????????????",
                gitUrl = "test git url",
                webSiteUrl = "test web site url",
                groupName = "test group",
                pictureUrl = "testUrl",
                point = 0,
                popularity = 0,
            )
        )

        beforeSaveIssueId = issueService.saveIssue(
            Issue(
                id = null,
                title = "Before Test Title",
                content = "Before Test content",
                docType = DocType.TEXT,
                category = "BACK-END"
            ),
            beforeSaveSolutionEmail,
            mutableListOf("test")
        ).id!!

        // ????????? ????????? ??????
        beforeSaveSolutionId = solutionService.saveSolution(
            Solution(
                id = null,
                content = "Before Test content",
                docType = DocType.TEXT
            ),
            beforeSaveSolutionEmail,
            beforeSaveIssueId
        ).id!!

        solutionService.recommendSolution(beforeSaveSolutionId, beforeSaveSolutionEmail)
        commentService.saveComment(
            Comment(
                id = null,
                content = "test comment",
            ),
            beforeSaveSolutionEmail,
            beforeSaveSolutionId
        )

        // ?????? ?????????????????? id ??????
        updateDto.id = beforeSaveSolutionId;
        saveDto.issueId = beforeSaveIssueId;

        //solutionService.findAllSolutionByIssueId(PageRequest.of(0, 5), beforeSaveIssueId)
        //commentService.findAllComment(PageRequest.of(0, 5), beforeSaveSolutionId)
    }

    @AfterEach
    fun printAllList() {

        log.debug("=========AfterEach========")
        var list: List<Solution> = solutionService.findAllSolution();

        for (item in list) {
            log.debug(item.toString())
        }
    }

    @Test
    fun testSave() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .content(mapper.writeValueAsString(saveDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "save-solution",
                    requestFields(
                        fieldWithPath("content").description("?????? ??????"),
                        fieldWithPath("docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("issueId").description("?????? ?????? ??????"),
                    )
                )

            )
    }

    @Test
    fun testSaveValidation() {
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .content("{\"content\":\"\",\"docType\":\"TEXT\",\"issueId\":135}")
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun testFind() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/{id}", beforeSaveSolutionId)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-solution",
                    pathParameters(
                        parameterWithName("id").description("????????????")
                    ),
                    responseFields(
                        fieldWithPath("id").description("????????????"),
                        fieldWithPath("issueId").description("?????? ?????????"),
                        fieldWithPath("content").description("??????"),
                        fieldWithPath("docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("recommendationCount").description("?????????"),
                        fieldWithPath("adoptYn").description("????????????"),
                        fieldWithPath("developer.email").description("????????? ?????????"),
                        fieldWithPath("developer.name").description("????????? ??????"),
                        fieldWithPath("developer.introduction").description("????????? ??????"),
                        fieldWithPath("developer.gitUrl").description("????????? ?????????"),
                        fieldWithPath("developer.webSiteUrl").description("????????? ????????????(?????????) ??????"),
                        fieldWithPath("developer.groupName").description("????????? ??????"),
                        fieldWithPath("developer.pictureUrl").description("????????? ????????????"),
                        fieldWithPath("developer.point").description("????????? ??????"),
                        fieldWithPath("developer.popularity").description("????????? ?????????"),
                        fieldWithPath("comment.[].id").description("????????? ?????? ?????????"),
                        fieldWithPath("comment.[].content").description("????????? ??????"),
                        fieldWithPath("comment.[].modifiedDate").description("????????? ??????????????????"),
                        fieldWithPath("comment.[].developer.email").description("????????? ????????? ?????????"),
                        fieldWithPath("comment.[].developer.name").description("????????? ????????? ??????"),
                        fieldWithPath("comment.[].developer.introduction").description("????????? ????????? ??????"),
                        fieldWithPath("comment.[].developer.gitUrl").description("????????? ????????? ?????????"),
                        fieldWithPath("comment.[].developer.webSiteUrl").description("????????? ????????? ????????????(?????????) ??????"),
                        fieldWithPath("comment.[].developer.groupName").description("????????? ????????? ??????"),
                        fieldWithPath("comment.[].developer.pictureUrl").description("????????? ????????? ????????????"),
                        fieldWithPath("comment.[].developer.point").description("????????? ????????? ?????????"),
                        fieldWithPath("comment.[].developer.popularity").description("????????? ????????? ?????????"),
                        fieldWithPath("modifiedDate").description("??????????????????"),
                    )
                )
            )
    }

    @Test
    fun testFindList() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/list/$beforeSaveIssueId")
                .param("page", "0")
                .param("size", "5")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-solution-list",
                    requestParameters(
                        parameterWithName("page").description("?????? ?????????"),
                        parameterWithName("size").description("?????? ????????? ?????????")
                    ),
                    responseFields(
                        fieldWithPath("content.[].id").description("????????????"),
                        fieldWithPath("content.[].content").description("??????"),
                        fieldWithPath("content.[].issueId").description("?????? ?????????"),
                        fieldWithPath("content.[].docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("content.[].recommendationCount").description("?????????"),
                        fieldWithPath("content.[].adoptYn").description("????????????"),
                        fieldWithPath("content.[].developer.email").description("????????? ?????????"),
                        fieldWithPath("content.[].developer.name").description("????????? ??????"),
                        fieldWithPath("content.[].developer.introduction").description("????????? ??????"),
                        fieldWithPath("content.[].developer.gitUrl").description("????????? ?????????"),
                        fieldWithPath("content.[].developer.webSiteUrl").description("????????? ????????????(?????????) ??????"),
                        fieldWithPath("content.[].developer.groupName").description("????????? ??????"),
                        fieldWithPath("content.[].developer.pictureUrl").description("????????? ????????????"),
                        fieldWithPath("content.[].developer.point").description("????????? ??????"),
                        fieldWithPath("content.[].developer.popularity").description("????????? ?????????"),
                        fieldWithPath("content.[].comment.[].id").description("????????? ?????? ?????????"),
                        fieldWithPath("content.[].comment.[].content").description("????????? ??????"),
                        fieldWithPath("content.[].comment.[].modifiedDate").description("????????? ??????????????????"),
                        fieldWithPath("content.[].comment.[].developer.email").description("????????? ????????? ?????????"),
                        fieldWithPath("content.[].comment.[].developer.name").description("????????? ????????? ??????"),
                        fieldWithPath("content.[].comment.[].developer.introduction").description("????????? ????????? ??????"),
                        fieldWithPath("content.[].comment.[].developer.gitUrl").description("????????? ????????? ?????????"),
                        fieldWithPath("content.[].comment.[].developer.webSiteUrl").description("????????? ????????? ????????????(?????????) ??????"),
                        fieldWithPath("content.[].comment.[].developer.groupName").description("????????? ????????? ??????"),
                        fieldWithPath("content.[].comment.[].developer.pictureUrl").description("????????? ????????? ????????????"),
                        fieldWithPath("content.[].comment.[].developer.point").description("????????? ????????? ?????????"),
                        fieldWithPath("content.[].comment.[].developer.popularity").description("????????? ????????? ?????????"),
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
    fun testFindListByEmail() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/list/developer/$beforeSaveSolutionEmail")
                .param("page", "0")
                .param("size", "5")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-solution-list-my-solution",
                    requestParameters(
                        parameterWithName("page").description("?????? ?????????"),
                        parameterWithName("size").description("?????? ????????? ?????????")
                    ),
                    responseFields(
                        fieldWithPath("content.[].id").description("????????????"),
                        fieldWithPath("content.[].issueId").description("?????? ?????????"),
                        fieldWithPath("content.[].content").description("??????"),
                        fieldWithPath("content.[].docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("content.[].recommendationCount").description("?????????"),
                        fieldWithPath("content.[].adoptYn").description("????????????"),
                        fieldWithPath("content.[].developer.email").description("????????? ?????????"),
                        fieldWithPath("content.[].developer.name").description("????????? ??????"),
                        fieldWithPath("content.[].developer.introduction").description("????????? ??????"),
                        fieldWithPath("content.[].developer.gitUrl").description("????????? ?????????"),
                        fieldWithPath("content.[].developer.webSiteUrl").description("????????? ????????????(?????????) ??????"),
                        fieldWithPath("content.[].developer.groupName").description("????????? ??????"),
                        fieldWithPath("content.[].developer.pictureUrl").description("????????? ????????????"),
                        fieldWithPath("content.[].developer.point").description("????????? ??????"),
                        fieldWithPath("content.[].developer.popularity").description("????????? ?????????"),
                        fieldWithPath("content.[].comment.[].id").description("????????? ?????? ?????????"),
                        fieldWithPath("content.[].comment.[].content").description("????????? ??????"),
                        fieldWithPath("content.[].comment.[].modifiedDate").description("????????? ??????????????????"),
                        fieldWithPath("content.[].comment.[].developer.email").description("????????? ????????? ?????????"),
                        fieldWithPath("content.[].comment.[].developer.name").description("????????? ????????? ??????"),
                        fieldWithPath("content.[].comment.[].developer.introduction").description("????????? ????????? ??????"),
                        fieldWithPath("content.[].comment.[].developer.gitUrl").description("????????? ????????? ?????????"),
                        fieldWithPath("content.[].comment.[].developer.webSiteUrl").description("????????? ????????? ????????????(?????????) ??????"),
                        fieldWithPath("content.[].comment.[].developer.groupName").description("????????? ????????? ??????"),
                        fieldWithPath("content.[].comment.[].developer.pictureUrl").description("????????? ????????? ????????????"),
                        fieldWithPath("content.[].comment.[].developer.point").description("????????? ????????? ?????????"),
                        fieldWithPath("content.[].comment.[].developer.popularity").description("????????? ????????? ?????????"),
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
    fun testUpdate() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.put(uri)
                .content(mapper.writeValueAsString(updateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "update-solution",
                    requestFields(
                        fieldWithPath("id").description("????????????"),
                        fieldWithPath("content").description("?????? ??????"),
                        fieldWithPath("docType").description("???????????? ('TEXT', 'MARK_DOWN')"),
                    )
                )
            )
    }

    @Test
    fun testDelete() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .delete("$uri/{id}", beforeSaveSolutionId)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "delete-solution",
                    pathParameters(
                        parameterWithName("id").description("????????????")
                    ),
                )
            )
    }

    @Test
    fun testRecommend() {
        mockMvc.perform(
            RestDocumentationRequestBuilders
                .put("$uri/recommend/{id}", beforeSaveSolutionId)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "recommend-solution",
                    pathParameters(
                        parameterWithName("id").description("????????????")
                    ),
                )
            )
    }


    @Test
    fun testAdopt() {
        mockMvc.perform(
            RestDocumentationRequestBuilders
                .put("$uri/adopt/{id}", beforeSaveSolutionId)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "adopt-solution",
                    pathParameters(
                        parameterWithName("id").description("????????????")
                    ),
                )
            )
    }
}
