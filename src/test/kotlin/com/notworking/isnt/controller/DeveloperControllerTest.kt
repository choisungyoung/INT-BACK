package com.notworking.isnt.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.notworking.isnt.controller.developer.dto.DeveloperSaveRequestDTO
import com.notworking.isnt.controller.developer.dto.DeveloperUpdateRequestDTO
import com.notworking.isnt.model.Developer
import com.notworking.isnt.service.DeveloperService
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

private val log = KotlinLogging.logger {}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest
@AutoConfigureMockMvc
class DeveloperControllerTest(@Autowired var developerService: DeveloperService) {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    private var uri: String = "/api/developer";
    private val saveDeveloperDTO: DeveloperSaveRequestDTO =
        DeveloperSaveRequestDTO(
            email = "tjddud117@naver.com",
            password = "aa12345^",
            name = "sungyoung",
            introduction = "안녕하세요",
            pictureUrl = "testUrl",
            point = 0,
            popularity = 0,
        )
    private val updateDeveloperDTO: DeveloperUpdateRequestDTO =
        DeveloperUpdateRequestDTO(
            email = "tjddud117@naver.com",
            password = "aa12345^",
            name = "sungyoung",
            introduction = "반갑습니다.",
            pictureUrl = "testUrl",
            point = 0,
            popularity = 0,
        )
    private val findDeveloperEmail: String = "tjddud117@naver.com"
    private val notFindDeveloperEmail: String = "tjddud118@naver.com"
    private val deleteDeveloperEmail: String = "tjddud117@naver.com"

    @AfterEach
    fun printDevelopers() {

        log.debug("=========START AfterEach=======")
        var developerList: List<Developer> = developerService.findAllDeveloper();

        for (developer in developerList) {
            log.debug(developer.toString())
        }
    }

    @Order(1)
    @Test
    fun testSave() {
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .content(mapper.writeValueAsString(saveDeveloperDTO))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }

    @Order(2)
    @Test
    fun testFind() {
        uri += "/$findDeveloperEmail"
        mockMvc.perform(
            MockMvcRequestBuilders.get(uri)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }

    @Order(3)
    @Test
    fun testNotFind() {
        uri += "/$notFindDeveloperEmail"
        mockMvc.perform(
            MockMvcRequestBuilders.get(uri)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
    }

    @Order(4)
    @Test
    fun testUpdate() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(uri)
                .content(mapper.writeValueAsString(updateDeveloperDTO))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }

    @Order(5)
    @Test
    fun testDelete() {
        uri += "/$deleteDeveloperEmail"
        mockMvc.perform(
            MockMvcRequestBuilders.delete(uri)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }
}
