package com.notworking.isnt.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.CommonMvcTest
import com.notworking.isnt.model.Developer
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.support.provider.JwtTokenProvider
import mu.KotlinLogging
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

private val log = KotlinLogging.logger {}

class CommonControllerTest(
    @Autowired var developerService: DeveloperService,
) : CommonMvcTest() {
    val uri: String = "/api/common"

    private val beforeSaveEmail = "commonTester@naver.com"

    @BeforeEach
    fun beforeEach() {

        developerService.saveDeveloper(
            Developer(
                id = null,
                email = beforeSaveEmail,
                pwd = "aa12345^",
                name = "commonTester",
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
    fun testRefreshToken() {
        var objectMapper = ObjectMapper()
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("$uri/refreshtoken")
                .header(
                    JwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.buildAccessToken(beforeSaveEmail)
                )
        )
            .andExpect(SecurityMockMvcResultMatchers.authenticated())
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "refreshtoken",
                    responseFields(
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("name").description("이름"),
                        fieldWithPath("introduction").description("소개"),
                        fieldWithPath("gitUrl").description("작성자 깃주소"),
                        fieldWithPath("webSiteUrl").description("작성자 웹사이트(블로그) 주소"),
                        fieldWithPath("groupName").description("작성자 소속"),
                        fieldWithPath("pictureUrl").description("사진경로"),
                        fieldWithPath("point").description("점수"),
                        fieldWithPath("popularity").description("인기도"),
                    )
                )
            )
    }


    @Test
    fun testFindIniData() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/initData")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-init-data",
                    responseFields(
                        fieldWithPath("categorys.[].code").description("카테고리 코드"),
                        fieldWithPath("categorys.[].key").description("카테고리 키"),
                        fieldWithPath("categorys.[].value").description("카테고리 값"),
                    )
                )
            )
    }
}
