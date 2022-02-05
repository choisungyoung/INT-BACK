package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Developer
import com.notworking.isnt.service.DeveloperService
import mu.KotlinLogging
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

private val log = KotlinLogging.logger {}

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Transactional
@SpringBootTest
class DeveloperServiceImplTest(@Autowired var developerService: DeveloperService) {


    companion object {

        lateinit var saveDeveloperList: List<Developer>
        lateinit var updateDeveloperName: String

        lateinit var findDeveloperUserId: String
        lateinit var notFindDeveloperUserId: String

        @BeforeAll
        @JvmStatic
        fun setupFeild() {
            saveDeveloperList = listOf(
                Developer(
                    id = null,
                    userId = "testService01",
                    email = "testService01@naver.com",
                    pwd = "aa12345^",
                    name = "test",
                    introduction = "안녕하세요",
                    gitUrl = "test git url",
                    webSiteUrl = "test web site url",
                    pictureUrl = "testUrl",
                    point = 0,
                    popularity = 0,
                ),
                Developer(
                    id = null,
                    userId = "testService02",
                    email = "testService02@nave.com",
                    pwd = "aa12345^",
                    name = "test",
                    introduction = "안녕하세요",
                    gitUrl = "test git url",
                    webSiteUrl = "test web site url",
                    pictureUrl = "testUrl",
                    point = 0,
                    popularity = 0,
                )
            )
            findDeveloperUserId = "testService01"
            updateDeveloperName = "updatetest"
            notFindDeveloperUserId = "testService03"
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
        developerService.findDeveloperByUserId(findDeveloperUserId)
    }

    @Order(3)
    @Test
    fun testUpdateDeveloper() {

        var developer: Developer? = developerService.findDeveloperByUserId(findDeveloperUserId)
        developer?.let {
            it.name = updateDeveloperName
            developerService.updateDeveloper(it)
        }
    }

    @Order(4)
    @Test
    fun testDeleteDeveloper() {
        var developer: Developer? = developerService.findDeveloperByUserId(findDeveloperUserId)
        developer?.email?.let {
            developerService.deleteDeveloper(it)
        }
    }

    @Order(5)
    @Test
    fun testNotFindDeveloper() {
        var developer: Developer? = developerService.findDeveloperByUserId(notFindDeveloperUserId)

        log.debug(developer.toString())
    }
}
