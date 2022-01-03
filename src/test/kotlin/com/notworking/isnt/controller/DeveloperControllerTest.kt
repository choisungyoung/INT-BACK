package com.notworking.isnt.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.controller.developer.dto.DeveloperSaveRequestDTO
import com.notworking.isnt.controller.developer.dto.DeveloperUpdateRequestDTO
import com.notworking.isnt.model.Developer
import com.notworking.isnt.service.DeveloperService
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
class DeveloperControllerTest(@Autowired var developerService: DeveloperService) {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    private var uri: String = "/api/developer";

    private val beforeEachDeveloperEmail: String = "test@naver.com"
    private val findDeveloperEmail: String = "test@naver.com"
    private val notFindDeveloperEmail: String = "failedTest@naver.com"
    private val saveDeveloperEmail: String = "testSave@naver.com"
    private val deleteDeveloperEmail: String = "test@naver.com"

    private val saveDTO: DeveloperSaveRequestDTO =
        DeveloperSaveRequestDTO(
            email = saveDeveloperEmail,
            password = "aa12345^",
            name = "테스터01",
            introduction = "안녕하세요",
        )
    private val updateDeveloperDTO: DeveloperUpdateRequestDTO =
        DeveloperUpdateRequestDTO(
            email = findDeveloperEmail,
            password = "aa12345^",
            name = "sungyoung",
            introduction = "반갑습니다.",
            pictureUrl = "testUrl",
            point = 0,
            popularity = 0,
        )

    @BeforeEach
    fun beforeEach() {
        // 사용자 추가
        developerService.saveDeveloper(
            Developer(
                id = null,
                email = beforeEachDeveloperEmail,
                pwd = "aa12345^",
                name = "test",
                introduction = "안녕하세요",
                pictureUrl = "testUrl",
                point = 0,
                popularity = 0,
            )
        )
    }

    @AfterEach
    fun printDevelopers() {

        log.debug("=========START AfterEach=======")
        var developerList: List<Developer> = developerService.findAllDeveloper();

        for (developer in developerList) {
            log.debug(developer.toString())
        }
    }

    @Test
    fun testSave() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .content(mapper.writeValueAsString(saveDTO))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "save-developer",
                    requestFields(
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("password").description("패스워드"),
                        fieldWithPath("name").description("이름"),
                        fieldWithPath("introduction").description("소개")
                    )
                )

            )
    }

    @Test
    fun testSaveValidation() {
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .content("{\"email\":\"\",\"password\":\"aa12345^\",\"name\":\"테스터01\",\"introduction\":\"안녕하세요\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun testFindList() {

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("$uri/list")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-developer-list",
                    responseFields(
                        fieldWithPath("[].email").description("이메일"),
                        fieldWithPath("[].name").description("이름"),
                        fieldWithPath("[].introduction").description("소개"),
                        fieldWithPath("[].pictureUrl").description("사진경로"),
                        fieldWithPath("[].point").description("점수"),
                        fieldWithPath("[].popularity").description("인기도"),
                    )
                )
            )
    }

    @Test
    fun testFind() {

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("$uri/{email}", findDeveloperEmail)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-developer",
                    pathParameters(
                        parameterWithName("email").description("이메일")
                    ),
                    responseFields(
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("name").description("이름"),
                        fieldWithPath("introduction").description("소개"),
                        fieldWithPath("pictureUrl").description("사진경로"),
                        fieldWithPath("point").description("점수"),
                        fieldWithPath("popularity").description("인기도"),
                    )
                )
            )
    }

    @Test
    fun testNotFind() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("$uri/{email}", notFindDeveloperEmail)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun testUpdate() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.put(uri)
                .content(mapper.writeValueAsString(updateDeveloperDTO))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "update-developer",
                    requestFields(
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("password").description("패스워드"),
                        fieldWithPath("name").description("이름"),
                        fieldWithPath("introduction").description("소개"),
                        fieldWithPath("pictureUrl").description("사진경로"),
                        fieldWithPath("point").description("점수"),
                        fieldWithPath("popularity").description("인기도")
                    )
                )
            )
    }

    @Test
    fun testDelete() {

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("$uri/{email}", deleteDeveloperEmail)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "delete-developer",
                    pathParameters(
                        parameterWithName("email").description("이메일")
                    ),
                )
            )
    }
}
