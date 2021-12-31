package com.notworking.isnt.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.controller.issue.dto.IssueSaveRequestDTO
import com.notworking.isnt.controller.issue.dto.IssueUpdateRequestDTO
import com.notworking.isnt.model.Developer
import com.notworking.isnt.model.Issue
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.support.type.DocType
import mu.KotlinLogging
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "localhost")
class IssueControllerTest(
    @Autowired var issueService: IssueService,
    @Autowired var developerService: DeveloperService
) {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    private var uri: String = "/api/issue"

    private val beforeSaveIssueEmail = "saveIssueTester@naver.com"
    private var beforeSaveIssueId: Long = 0
    private val notFindIssueId: Long = -999

    private val saveDto = IssueSaveRequestDTO(
        title = "Test Title",
        content = "test content",
        docType = DocType.TEXT.code,
    )

    private val updateDto = IssueUpdateRequestDTO(
        id = 0,     // beforeEach에서 재설정
        title = "Update Test Title",
        content = "update test content",
        docType = DocType.MARK_DOWN.code
    )

    @BeforeEach
    fun beforeEach() {

        developerService.saveDeveloper(
            Developer(
                id = null,
                email = beforeSaveIssueEmail,
                pwd = "aa12345^",
                name = "test",
                introduction = "안녕하세요",
                pictureUrl = "testUrl",
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
            beforeSaveIssueEmail
        ).id!!

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
                    )
                )

            )
    }

    @Test
    fun testFindList() {

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("$uri/list/latest")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-issue-list-latest",
                    responseFields(
                        fieldWithPath("[].id").description("고유번호"),
                        fieldWithPath("[].title").description("제목"),
                        fieldWithPath("[].content").description("내용"),
                        fieldWithPath("[].docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("[].hits").description("조회수"),
                        fieldWithPath("[].recommendationCount").description("추천수"),
                    )
                )
            )
    }

    @Test
    fun testFind() {

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("$uri/{id}", beforeSaveIssueId)
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
                    )
                )
            )
    }

    @Test
    fun testDelete() {

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("$uri/{id}", beforeSaveIssueId)
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
