package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Developer
import com.notworking.isnt.service.DeveloperService
import mu.KotlinLogging
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

private val log = KotlinLogging.logger {}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Transactional
@SpringBootTest
class DeveloperServiceImplTest(@Autowired var developerService: DeveloperService) {


    companion object {

        lateinit var saveDeveloperList: List<Developer>
        lateinit var findDeveloperEmail: String
        lateinit var updateDeveloperName: String
        lateinit var notFindDeveloperEmail: String

        @BeforeAll
        @JvmStatic
        fun setupFeild() {
            saveDeveloperList = listOf(
                Developer(
                    id = null,
                    email = "testService01@naver.com",
                    pwd = "aa12345^",
                    name = "test",
                    introduction = "안녕하세요",
                    pictureUrl = "testUrl",
                    point = 0,
                    popularity = 0,
                ),
                Developer(
                    id = null,
                    email = "testService02@nave.com",
                    pwd = "aa12345^",
                    name = "test",
                    introduction = "안녕하세요",
                    pictureUrl = "testUrl",
                    point = 0,
                    popularity = 0,
                )
            )
            findDeveloperEmail = "testService01@naver.com"
            updateDeveloperName = "updatetest"
            notFindDeveloperEmail = "testService03@naver.com"
        }
    }

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

        var developer: Developer? = developerService.findDeveloperByEmail(findDeveloperEmail)
        developer?.let {
            it.name = updateDeveloperName
            developerService.updateDeveloper(it)
        }
    }

    @Order(4)
    @Test
    fun testDeleteDeveloper() {
        var developer: Developer? = developerService.findDeveloperByEmail(findDeveloperEmail)
        developer?.email?.let {
            developerService.deleteDeveloper(it)
        }
    }

    @Order(5)
    @Test
    fun testNotFindDeveloper() {
        var developer: Developer? = developerService.findDeveloperByEmail(notFindDeveloperEmail)

        log.debug(developer.toString())
    }
}
