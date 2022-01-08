package com.notworking.isnt.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.controller.issue.dto.IssueSaveRequestDTO
import com.notworking.isnt.controller.issue.dto.IssueUpdateRequestDTO
import com.notworking.isnt.model.Developer
import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.Solution
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.service.SolutionService
import com.notworking.isnt.support.type.DocType
import mu.KotlinLogging
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
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
    @Autowired var developerService: DeveloperService,
    @Autowired var issueService: IssueService,
    @Autowired var solutionService: SolutionService,
) {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    private var uri: String = "/api/issue"

    private val beforeSaveIssueEmail = "issueTester@naver.com"
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

        solutionService.saveSolution(
            Solution(
                id = null,
                content = "test solution",
                docType = DocType.TEXT
            ),
            beforeSaveIssueEmail,
            beforeSaveIssueId
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
    fun testSaveValidation() {
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .content("{\"title\":\"\",\"content\":\"test content\",\"docType\":\"TEXT\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun testFindList() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/list/latest")
                .param("page", "1")
                .param("size", "5")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-issue-list-latest",
                    requestParameters(
                        parameterWithName("page").description("조회 페이지"),
                        parameterWithName("size").description("조회 페이지 사이즈")
                    ),
                    responseFields(
                        fieldWithPath("content.[].id").description("고유번호"),
                        fieldWithPath("content.[].title").description("제목"),
                        fieldWithPath("content.[].content").description("내용"),
                        fieldWithPath("content.[].docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("content.[].hits").description("조회수"),
                        fieldWithPath("content.[].recommendationCount").description("추천수"),
                        fieldWithPath("content.[].developer.email").description("작성자 이메일"),
                        fieldWithPath("content.[].developer.name").description("작성자 이름"),
                        fieldWithPath("content.[].developer.introduction").description("작성자 소개"),
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
                beforeSaveIssueEmail
            )
        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/list/latest")
                .param("page", "2")
                .param("size", "5")
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
                        fieldWithPath("developer.email").description("작성자 이메일"),
                        fieldWithPath("developer.name").description("작성자 이름"),
                        fieldWithPath("developer.introduction").description("작성자 소개"),
                        fieldWithPath("developer.pictureUrl").description("작성자 사진경로"),
                        fieldWithPath("developer.point").description("작성자 점수"),
                        fieldWithPath("developer.popularity").description("작성자 인기도"),
                        fieldWithPath("modifiedDate").description("최종수정일시"),
                        fieldWithPath("solutions.[].id").description("고유번호"),
                        fieldWithPath("solutions.[].content").description("내용"),
                        fieldWithPath("solutions.[].docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("solutions.[].recommendationCount").description("추천수"),
                        fieldWithPath("solutions.[].developer.email").description("작성자 이메일"),
                        fieldWithPath("solutions.[].developer.name").description("작성자 이름"),
                        fieldWithPath("solutions.[].developer.introduction").description("작성자 소개"),
                        fieldWithPath("solutions.[].developer.pictureUrl").description("작성자 사진경로"),
                        fieldWithPath("solutions.[].developer.point").description("작성자 점수"),
                        fieldWithPath("solutions.[].developer.popularity").description("작성자 인기도"),
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
