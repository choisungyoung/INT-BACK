package com.notworking.isnt.controller

import com.notworking.isnt.model.Developer
import com.notworking.isnt.service.DeveloperService
import mu.KotlinLogging
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
class AuthControllerTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var developerService: DeveloperService

    private var loginUsername: String = "testLogin"
    private var loginEmail: String = "testLogin@naver.com"
    private var loginPassword: String = "aa12345^"

    private var failLoginUsername: String = "testLogin@naver.com"
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

    @Order(1)
    @Test
    fun testLoginSuccess() {
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
