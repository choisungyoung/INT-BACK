package com.notworking.isnt.repository

import com.notworking.isnt.model.Developer
import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.Solution
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SolutionRepository : JpaRepository<Solution, Long> {
    fun findAllByDeveloper(developer: Developer): List<Solution>

    fun findAllByIssueAndAdoptYn(issue: Issue, adoptYn: Boolean): List<Solution>

    /*
        @EntityGraph(
            value = "Solution.withComment",
            attributePaths = ["comments"],
            type = EntityGraph.EntityGraphType.LOAD
        )*/
    fun findSolutionAndCommentWithCommentsByIssueId(pageable: Pageable, issueId: Long): Page<Solution>
}