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

private val logger = KotlinLogging.logger {}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {

        lateinit var loginUsername: String
        lateinit var loginPassword: String

        lateinit var failLoginUsername: String
        lateinit var failLoginPassword: String

        @BeforeAll
        @JvmStatic
        fun beforeAll(@Autowired developerService: DeveloperService) {
            loginUsername = "test@naver.com"
            loginPassword = "aa12345^"

            failLoginUsername = "test@naver.com"
            failLoginPassword = "aa12345^^"

            // 사용자 추가
            developerService.saveDeveloper(
                Developer(
                    id = null,
                    email = "tjddud117@naver.com",
                    pwd = "aa12345^",
                    name = "sungyoung",
                    introduction = "안녕하세요",
                    pictureUrl = "testUrl",
                    point = 0,
                    popularity = 0,
                )
            )

        }
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
