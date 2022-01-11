package com.notworking.isnt.repository

import com.notworking.isnt.repository.support.SolutionRepositorySupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest

@SpringBootTest
internal class SolutionRepositorySupportTest(
    @Autowired var solutionRepositorySupport: SolutionRepositorySupport,
) {

    @Test
    fun findIssueJoinSolution() {
        var soltuion = solutionRepositorySupport.findSolutionByIssueId(PageRequest.of(0, 10), 29)
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