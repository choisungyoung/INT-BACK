package com.notworking.isnt.controller

import com.notworking.isnt.CommonMvcTest
import com.notworking.isnt.controller.issue.dto.CommentSaveRequestDTO
import com.notworking.isnt.controller.issue.dto.CommentUpdateRequestDTO
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

class CommentControllerTest(
    @Autowired var developerService: DeveloperService,
    @Autowired var issueService: IssueService,
    @Autowired var solutionService: SolutionService,
    @Autowired var commentService: CommentService,
) : CommonMvcTest() {

    private var uri: String = "/api/comment"

    private val beforeSaveSolutionEmail = "commentTester@naver.com"
    private var beforeSaveSolutionId: Long = 0
    private var beforeSaveIssueId: Long = 0
    private var beforeSaveCommentId: Long = 0

    private val saveDto = CommentSaveRequestDTO(
        solutionId = 0, // beforeEach?????? ?????????
        content = "test content",
    )

    private val updateDto = CommentUpdateRequestDTO(
        id = 0,     // beforeEach?????? ?????????
        content = "update test content",
    )

    @BeforeEach
    fun beforeEach() {

        developerService.saveDeveloper(
            Developer(
                id = null,
                email = beforeSaveSolutionEmail,
                pwd = "aa12345^",
                name = "commentTester",
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

        beforeSaveCommentId = commentService.saveComment(
            Comment(
                id = null,
                content = "test comment",
            ),
            beforeSaveSolutionEmail,
            beforeSaveSolutionId
        ).id!!

        // ?????? ?????????????????? id ??????
        updateDto.id = beforeSaveCommentId
        saveDto.solutionId = beforeSaveSolutionId
    }

    @AfterEach
    fun printAllList() {

        log.debug("=========AfterEach========")
        var list: List<Comment> = commentService.findAllComment();

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
                    "save-comment",
                    requestFields(
                        fieldWithPath("solutionId").description("????????? ????????????"),
                        fieldWithPath("content").description("????????? ??????"),
                    )
                )

            )
    }

    @Test
    fun testSaveValidation() {
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .content("{\"content\":\"\",\"solutionId\":135}")
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun testFindList() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/list/$beforeSaveSolutionId")
                .param("page", "0")
                .param("size", "5")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-comment-list",
                    requestParameters(
                        parameterWithName("page").description("?????? ?????????"),
                        parameterWithName("size").description("?????? ????????? ?????????")
                    ),
                    responseFields(
                        fieldWithPath("content.[].id").description("????????? ?????? ?????????"),
                        fieldWithPath("content.[].content").description("????????? ??????"),
                        fieldWithPath("content.[].modifiedDate").description("????????? ??????????????????"),
                        fieldWithPath("content.[].developer.email").description("????????? ????????? ?????????"),
                        fieldWithPath("content.[].developer.name").description("????????? ????????? ??????"),
                        fieldWithPath("content.[].developer.introduction").description("????????? ????????? ??????"),
                        fieldWithPath("content.[].developer.gitUrl").description("????????? ????????? ?????????"),
                        fieldWithPath("content.[].developer.webSiteUrl").description("????????? ????????? ????????????(?????????) ??????"),
                        fieldWithPath("content.[].developer.groupName").description("????????? ????????? ??????"),
                        fieldWithPath("content.[].developer.pictureUrl").description("????????? ????????? ????????????"),
                        fieldWithPath("content.[].developer.point").description("????????? ????????? ?????????"),
                        fieldWithPath("content.[].developer.popularity").description("????????? ????????? ?????????"),
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
                    "update-comment",
                    requestFields(
                        fieldWithPath("id").description("????????????"),
                        fieldWithPath("content").description("?????? ??????"),
                    )
                )
            )
    }

    @Test
    fun testDelete() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .delete("$uri/{id}", beforeSaveCommentId)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionEmail)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "delete-comment",
                    pathParameters(
                        parameterWithName("id").description("????????????")
                    ),
                )
            )
    }
}
