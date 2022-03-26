package com.notworking.isnt.controller

import com.notworking.isnt.CommonMvcTest
import com.notworking.isnt.model.Code
import com.notworking.isnt.service.CodeService
import mu.KotlinLogging
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

private val log = KotlinLogging.logger {}

class CodeControllerTest(
    @Autowired var codeService: CodeService,
) : CommonMvcTest() {

    private var uri: String = "/api/code"
    private var beforeSaveCode = "CATEGORY"

    @BeforeEach
    fun beforeEach() {
        codeService.saveCode(
            Code(
                id = null,
                code = "TEST",
                key = "01",
                value = "value01",
                name = "테스트",
                description = "설명",
            )
        )
        codeService.saveCode(
            Code(
                id = null,
                code = "TEST",
                key = "02",
                value = "value02",
                name = "테스트",
                description = "설명",
            )
        )
    }


    @Test
    fun testFindList() {

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("$uri/{code}", beforeSaveCode)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "find-code",
                    pathParameters(
                        parameterWithName("code").description("조회 코드"),
                    ),
                    responseFields(
                        fieldWithPath("[].code").description("코드"),
                        fieldWithPath("[].key").description("키"),
                        fieldWithPath("[].value").description("값"),

                        )
                )
            )
    }
}
