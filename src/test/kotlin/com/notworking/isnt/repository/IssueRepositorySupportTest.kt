package com.notworking.isnt.repository

import com.notworking.isnt.repository.support.IssueRepositorySupport
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest

@Disabled
@SpringBootTest
internal class IssueRepositorySupportTest(
    @Autowired var issueRepositorySupport: IssueRepositorySupport,
    @Autowired var issueRepository: IssueRepository,
) {

    @Test
    fun findIssueJoinSolution() {
        var issue = issueRepositorySupport.findIssueByIdOtoM(2852)
        System.out.println(issue)
    }

    @Test
    fun findIssueByContentContains() {
        var issue = issueRepositorySupport.findAllIssueByTitleContentContains(
            PageRequest.of(
                0,
                10
            ), "가"
        )
        System.out.println(issue)
    }

    @Disabled
    @Test
    fun findAllIssuePage() {
        var issue = issueRepositorySupport.findAllIssuePage(
            PageRequest.of(
                0,
                10
            ), null
        )
        System.out.println(issue)
    }
}