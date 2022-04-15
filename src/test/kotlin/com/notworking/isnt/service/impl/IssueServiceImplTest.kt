package com.notworking.isnt.service.impl

import com.notworking.isnt.service.IssueService
import mu.KotlinLogging
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import javax.transaction.Transactional

private val log = KotlinLogging.logger {}

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Transactional
@SpringBootTest
class IssueServiceImplTest(@Autowired var issueService: IssueService) {

    @Test
    fun testFindAllIssuePage() {
        var issues = issueService.findAllIssue(
            PageRequest.of(
                0,
                10
            ),
            null,
            null
        )

        log.debug(issues.toString())
    }
}
