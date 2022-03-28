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

    private val beforeSaveSolutionUserId = "solutionTester"
    private var beforeSaveSolutionId: Long = 0
    private var beforeSaveIssueId: Long = 0

    private val saveDto = SolutionSaveRequestDTO(
        content = "test content",
        docType = DocType.TEXT.code,
        issueId = 0 // beforeEach에서 재설정
    )

    private val updateDto = SolutionUpdateRequestDTO(
        id = 0,     // beforeEach에서 재설정
        content = "update test content",
        docType = DocType.MARK_DOWN.code
    )

    @BeforeEach
    fun beforeEach() {

        developerService.saveDeveloper(
            Developer(
                id = null,
                userId = beforeSaveSolutionUserId,
                email = "beforeSaveSolutionEmail@naver.com",
                pwd = "aa12345^",
                name = "solutionTester",
                introduction = "안녕하세요",
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
            beforeSaveSolutionUserId,
            mutableListOf("test")
        ).id!!

        // 테스트 솔루션 추가
        beforeSaveSolutionId = solutionService.saveSolution(
            Solution(
                id = null,
                content = "Before Test content",
                docType = DocType.TEXT
            ),
            beforeSaveSolutionUserId,
            beforeSaveIssueId
        ).id!!

        solutionService.recommendSolution(beforeSaveSolutionId, beforeSaveSolutionUserId)
        commentService.saveComment(
            Comment(
                id = null,
                content = "test comment",
            ),
            beforeSaveSolutionUserId,
            beforeSaveSolutionId
        )

        // 수정 테스트케이스 id 설정
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
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionUserId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "save-solution",
                    requestFields(
                        fieldWithPath("content").description("이슈 내용"),
                        fieldWithPath("docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("issueId").description("이슈 고유 번호"),
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
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionUserId)
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
                        parameterWithName("id").description("고유번호")
                    ),
                    responseFields(
                        fieldWithPath("id").description("고유번호"),
                        fieldWithPath("issueId").description("이슈 아이디"),
                        fieldWithPath("content").description("내용"),
                        fieldWithPath("docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("recommendationCount").description("추천수"),
                        fieldWithPath("adoptYn").description("채택여부"),
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
                        fieldWithPath("comment.[].id").description("코멘트 고유 아이디"),
                        fieldWithPath("comment.[].content").description("코멘트 내용"),
                        fieldWithPath("comment.[].modifiedDate").description("코멘트 최종수정일시"),
                        fieldWithPath("comment.[].developer.userId").description("작성자 아이디"),
                        fieldWithPath("comment.[].developer.email").description("코멘트 작성자 이메일"),
                        fieldWithPath("comment.[].developer.name").description("코멘트 작성자 이름"),
                        fieldWithPath("comment.[].developer.introduction").description("코멘트 작성자 소개"),
                        fieldWithPath("comment.[].developer.gitUrl").description("코멘트 작성자 깃주소"),
                        fieldWithPath("comment.[].developer.webSiteUrl").description("코멘트 작성자 웹사이트(블로그) 주소"),
                        fieldWithPath("comment.[].developer.groupName").description("코멘트 작성자 소속"),
                        fieldWithPath("comment.[].developer.pictureUrl").description("코멘트 작성자 사진경로"),
                        fieldWithPath("comment.[].developer.point").description("코멘트 작성자 포인트"),
                        fieldWithPath("comment.[].developer.popularity").description("코멘트 작성자 인기도"),
                        fieldWithPath("modifiedDate").description("최종수정일시"),
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
                        parameterWithName("page").description("조회 페이지"),
                        parameterWithName("size").description("조회 페이지 사이즈")
                    ),
                    responseFields(
                        fieldWithPath("content.[].id").description("고유번호"),
                        fieldWithPath("content.[].content").description("내용"),
                        fieldWithPath("content.[].issueId").description("이슈 아이디"),
                        fieldWithPath("content.[].docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("content.[].recommendationCount").description("추천수"),
                        fieldWithPath("content.[].adoptYn").description("채택여부"),
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
                        fieldWithPath("content.[].comment.[].id").description("코멘트 고유 아이디"),
                        fieldWithPath("content.[].comment.[].content").description("코멘트 내용"),
                        fieldWithPath("content.[].comment.[].modifiedDate").description("코멘트 최종수정일시"),
                        fieldWithPath("content.[].comment.[].developer.userId").description("작성자 아이디"),
                        fieldWithPath("content.[].comment.[].developer.email").description("코멘트 작성자 이메일"),
                        fieldWithPath("content.[].comment.[].developer.name").description("코멘트 작성자 이름"),
                        fieldWithPath("content.[].comment.[].developer.introduction").description("코멘트 작성자 소개"),
                        fieldWithPath("content.[].comment.[].developer.gitUrl").description("코멘트 작성자 깃주소"),
                        fieldWithPath("content.[].comment.[].developer.webSiteUrl").description("코멘트 작성자 웹사이트(블로그) 주소"),
                        fieldWithPath("content.[].comment.[].developer.groupName").description("코멘트 작성자 소속"),
                        fieldWithPath("content.[].comment.[].developer.pictureUrl").description("코멘트 작성자 사진경로"),
                        fieldWithPath("content.[].comment.[].developer.point").description("코멘트 작성자 포인트"),
                        fieldWithPath("content.[].comment.[].developer.popularity").description("코멘트 작성자 인기도"),
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
    fun testFindListByUserId() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/list/developer/$beforeSaveSolutionUserId")
                .param("page", "0")
                .param("size", "5")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-solution-list-my-solution",
                    requestParameters(
                        parameterWithName("page").description("조회 페이지"),
                        parameterWithName("size").description("조회 페이지 사이즈")
                    ),
                    responseFields(
                        fieldWithPath("content.[].id").description("고유번호"),
                        fieldWithPath("content.[].issueId").description("이슈 아이디"),
                        fieldWithPath("content.[].content").description("내용"),
                        fieldWithPath("content.[].docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
                        fieldWithPath("content.[].recommendationCount").description("추천수"),
                        fieldWithPath("content.[].adoptYn").description("채택여부"),
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
                        fieldWithPath("content.[].comment.[].id").description("코멘트 고유 아이디"),
                        fieldWithPath("content.[].comment.[].content").description("코멘트 내용"),
                        fieldWithPath("content.[].comment.[].modifiedDate").description("코멘트 최종수정일시"),
                        fieldWithPath("content.[].comment.[].developer.userId").description("작성자 아이디"),
                        fieldWithPath("content.[].comment.[].developer.email").description("코멘트 작성자 이메일"),
                        fieldWithPath("content.[].comment.[].developer.name").description("코멘트 작성자 이름"),
                        fieldWithPath("content.[].comment.[].developer.introduction").description("코멘트 작성자 소개"),
                        fieldWithPath("content.[].comment.[].developer.gitUrl").description("코멘트 작성자 깃주소"),
                        fieldWithPath("content.[].comment.[].developer.webSiteUrl").description("코멘트 작성자 웹사이트(블로그) 주소"),
                        fieldWithPath("content.[].comment.[].developer.groupName").description("코멘트 작성자 소속"),
                        fieldWithPath("content.[].comment.[].developer.pictureUrl").description("코멘트 작성자 사진경로"),
                        fieldWithPath("content.[].comment.[].developer.point").description("코멘트 작성자 포인트"),
                        fieldWithPath("content.[].comment.[].developer.popularity").description("코멘트 작성자 인기도"),
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
    fun testUpdate() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.put(uri)
                .content(mapper.writeValueAsString(updateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionUserId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "update-solution",
                    requestFields(
                        fieldWithPath("id").description("고유번호"),
                        fieldWithPath("content").description("이슈 내용"),
                        fieldWithPath("docType").description("문서유형 ('TEXT', 'MARK_DOWN')"),
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
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionUserId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "delete-solution",
                    pathParameters(
                        parameterWithName("id").description("고유번호")
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
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionUserId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "recommend-solution",
                    pathParameters(
                        parameterWithName("id").description("고유번호")
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
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveSolutionUserId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "adopt-solution",
                    pathParameters(
                        parameterWithName("id").description("고유번호")
                    ),
                )
            )
    }
}
