package com.notworking.isnt.repository

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class IssueRepositorySupportTest(
    @Autowired var issueRepositorySupport: IssueRepositorySupport
) {

    @Test
    fun findSolutionJoinIssue() {
        var solution = issueRepositorySupport.findSolutionByIssueId(25)
        System.out.println(solution)
    }

    @Test
    fun findIssueJoinSolution() {
        var issue = issueRepositorySupport.findIssueByIdOtoM(25)
        System.out.println(issue)
    }
}