package com.notworking.int.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.int.controller.dto.LoginDTO
import com.notworking.int.model.Developer
import com.notworking.int.service.DeveloperService
import com.notworking.int.support.type.Role
import mu.KotlinLogging
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.util.LinkedMultiValueMap

private val logger = KotlinLogging.logger {}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var developerService: DeveloperService

    @Autowired
    private lateinit var objectMapper : ObjectMapper

    lateinit var username: String
    lateinit var password: String

    @BeforeEach
    fun setupFeild() {
        username = "tjddud117@naver.com"
        password = "aa12345^"

        // 사용자 추가
        developerService.saveDeveloper(
            Developer(
                id = null,
                email = "tjddud117@naver.com",
                pwd = "aa12345^",
                name = "sungyoung",
                pictureUrl = "testUrl",
            )
        )

    }

    @Order(1)
    @Test
    fun testLogin() {
        val uri: String = "/api/auth/login"

        val requestBody = LinkedMultiValueMap<String, String>()
        requestBody.add("username", username)
        requestBody.add("password", password)


        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .content(objectMapper.writeValueAsString(LoginDTO(username, password)))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }
}
