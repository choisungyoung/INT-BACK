package com.notworking.isnt.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.model.Developer
import com.notworking.isnt.service.DeveloperService
import mu.KotlinLogging
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "localhost")
class AuthControllerTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var developerService: DeveloperService

    private var loginUsername: String = "testLogin"
    private var loginEmail: String = "testLogin@naver.com"
    private var loginPassword: String = "aa12345^"

    private var failLoginUsername: String = "testLogin"
    private var failLoginPassword: String = "aa12345^^"

    @BeforeEach
    fun beforeEach() {

        // 사용자 추가
        developerService.saveDeveloper(
            Developer(
                id = null,
                userId = loginUsername,
                email = loginEmail,
                pwd = loginPassword,
                name = "sungyoung",
                introduction = "안녕하세요",
                gitUrl = "test git url",
                webSiteUrl = "test web site url",
                pictureUrl = "testUrl",
                point = 0,
                popularity = 0,
            )
        )
    }

    @Test
    fun testLoginSuccess() {
        val uri: String = "/api/auth/login"
        var objectMapper = ObjectMapper()
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .param("username", loginUsername)
                .param("password", loginPassword)

        )
            .andExpect(SecurityMockMvcResultMatchers.authenticated())
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "login",
                    responseFields(
                        fieldWithPath("userId").description("유저아이디"),
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("name").description("이름"),
                        fieldWithPath("introduction").description("소개"),
                        fieldWithPath("gitUrl").description("작성자 깃주소"),
                        fieldWithPath("webSiteUrl").description("작성자 웹사이트(블로그) 주소"),
                        fieldWithPath("pictureUrl").description("사진경로"),
                        fieldWithPath("point").description("점수"),
                        fieldWithPath("popularity").description("인기도"),
                    )
                )
            )
    }

    @Order(1)
    @Test
    fun testSecurityLoginSuccess() {
        val uri: String = "/api/auth/login"

        mockMvc.perform(
            SecurityMockMvcRequestBuilders.formLogin(uri).user(loginUsername).password(loginPassword)
        )
            .andExpect(SecurityMockMvcResultMatchers.authenticated())
            .andDo(MockMvcResultHandlers.print())
    }

    @Order(2)
    @Test
    fun testLoginFailure() {
        val uri: String = "/api/auth/login"

        mockMvc.perform(
            SecurityMockMvcRequestBuilders.formLogin(uri).user(failLoginUsername).password(failLoginPassword)
        )
            .andExpect(SecurityMockMvcResultMatchers.unauthenticated())
            .andDo(MockMvcResultHandlers.print())
    }
}
