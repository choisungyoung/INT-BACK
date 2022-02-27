package com.notworking.isnt.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.CommonMvcTest
import com.notworking.isnt.controller.issue.dto.AuthLoginRequestDTO
import com.notworking.isnt.model.Developer
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import mu.KotlinLogging
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

private val log = KotlinLogging.logger {}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AuthControllerTest() : CommonMvcTest() {

    @Autowired
    lateinit var developerService: DeveloperService

    @Value("\${spring.security.oauth2.client.registration.github.clientId}") // depth가 존재하는 값은 .으로 구분해서 값을 매핑
    lateinit var gitHubClientId: String

    var loginDto = AuthLoginRequestDTO(
        "testLogin",
        "aa12345^"
    )

    var loginFailDto = AuthLoginRequestDTO(
        "testLogin",
        "aa12345^^"
    )

    @BeforeEach
    fun beforeEach() {

        // 사용자 추가
        developerService.saveDeveloper(
            Developer(
                id = null,
                userId = loginDto.username,
                email = "loginEmail@naver.com",
                pwd = loginDto.password,
                name = "loginTester",
                introduction = "안녕하세요",
                gitUrl = "test git url",
                webSiteUrl = "test web site url",
                groupName = "test group",
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
                .content(mapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON)

        )
            .andExpect(MockMvcResultMatchers.status().isOk)
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
                        fieldWithPath("groupName").description("소속"),
                        fieldWithPath("pictureUrl").description("사진경로"),
                        fieldWithPath("point").description("점수"),
                        fieldWithPath("popularity").description("인기도"),
                    )
                )
            )
    }

    @Test
    fun testLoginFail() {
        val uri: String = "/api/auth/login"
        var objectMapper = ObjectMapper()
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .content(mapper.writeValueAsString(loginFailDto))
                .contentType(MediaType.APPLICATION_JSON)

        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun testSendAuthmail() {
        val uri = "/api/auth/sendAuthMail"

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("$uri/{userId}", "tjddud")

        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "auth-request-authNum",
                    pathParameters(
                        parameterWithName("userId").description("유저아이디")
                    ),

                    )
            )
    }

    @Test
    fun testCheckAuthNum() {

        var userId = "tjddud"
        var developer = developerService.findDeveloperByUserId(userId)
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        val uri = "/api/auth/checkAuthNum"

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("$uri/{userId}", userId)
                .param("authNum", developer.authNum.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "auth-check-authNum",

                    requestParameters(
                        parameterWithName("authNum").description("인증번호"),
                    ),
                    pathParameters(
                        parameterWithName("userId").description("유저아이디")
                    ),

                    responseFields(
                        fieldWithPath("successYn").description("인증 성공 여부"),
                    )
                    )
            )
    }

    /*
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

     */
}
