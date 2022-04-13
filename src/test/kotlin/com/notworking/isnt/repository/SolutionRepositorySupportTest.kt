package com.notworking.isnt.repository

import com.notworking.isnt.repository.support.SolutionRepositorySupport
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@Disabled
@SpringBootTest
@Transactional
internal class SolutionRepositorySupportTest(
    @Autowired var solutionRepositorySupport: SolutionRepositorySupport,
) {

    @Test
    fun findIssueJoinSolutionByIssueId() {
        var soltuion = solutionRepositorySupport.findSolutionByIssueId(PageRequest.of(0, 5), 5976)

        System.out.println(soltuion)
    }

    @Test
    fun findIssueJoinSolutionByUserId() {
        var soltuion = solutionRepositorySupport.findSolutionByEmail(PageRequest.of(0, 10), "")
        System.out.println(soltuion)
    }

    @Test
    fun findSolutionCount() {
        var cnt = solutionRepositorySupport.findSolutionCount(25)
        System.out.println(cnt)
    }

    @Test
    fun findSolutionAdoptYn() {
        var adoptYn = solutionRepositorySupport.findSolutionAdopt(25)
        System.out.println(adoptYn)
    }
}