package com.notworking.int.service.impl

import com.notworking.int.model.Developer
import com.notworking.int.service.DeveloperService
import com.notworking.int.support.type.Role
import mu.KotlinLogging
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger {}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@SpringBootTest
class DeveloperServiceImplTest(@Autowired var developerService: DeveloperService) {

    lateinit var saveDeveloperList: List<Developer>
    lateinit var findDeveloperEmail: String
    lateinit var updateDeveloperName: String

    @BeforeEach
    fun setupFeild() {
        saveDeveloperList = listOf(
            Developer(
                id = null,
                email = "tjddud117@naver.com",
                pwd = "aa12345^",
                name = "sungyoung",
                pictureUrl = "testUrl",
            ),
            Developer(
                id = null,
                email = "test@nave.com",
                pwd = "aa12345^",
                name = "test",
                pictureUrl = "testUrl",
            )
        )
        findDeveloperEmail = "tjddud117@naver.com"
        updateDeveloperName = "choisungyoung"
    }

    @AfterEach
    fun printDevelopers() {

        logger.debug("=========START AfterEach=======")
        var developerList : List<Developer> = developerService.findAllDeveloper();

        for (developer in developerList) {
            logger.debug(developer.toString())
        }
    }

    @Order(1)
    @Test
    fun testSaveDeveloper() {
        for (developer in saveDeveloperList) {
            developerService.saveDeveloper(developer)
        }
    }

    @Order(2)
    @Test
    fun testFindDeveloper() {
        developerService.findDeveloperByEmail(findDeveloperEmail)
    }

    @Order(3)
    @Test
    fun testUpdateDeveloper() {

        var developer : Developer? = developerService.findDeveloperByEmail(findDeveloperEmail)
        developer?.let {
            it.name = updateDeveloperName
            developerService.updateDeveloper(it)
        }
    }

    @Order(4)
    @Test
    fun testDeleteDeveloper() {
        var developer : Developer? = developerService.findDeveloperByEmail(findDeveloperEmail)
        developer?.id?.let {
            developerService.deleteDeveloper(it)
        }
    }
}
