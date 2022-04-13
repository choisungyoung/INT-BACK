package com.notworking.isnt.service

import com.notworking.isnt.model.Solution
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SolutionService {

    fun findSolution(id: Long): Solution?

    fun findAllSolution(): List<Solution>

    fun findAllSolutionByEmail(pageable: Pageable, email: String): Page<Solution>

    //fun findAllSolutionByUserId(pageable: Pageable, userId: String): Page<Solution>

    fun findAllSolutionByIssueId(pageable: Pageable, issueId: Long): Page<Solution>

    fun findAllSolution(issueId: Long): List<Solution>

    fun findSolutionCount(issueId: Long): Long

    fun findSolutionAdoptYn(issueId: Long): Boolean

    fun saveSolution(solution: Solution, userId: String, issueId: Long): Solution

    fun updateSolution(solution: Solution)

    fun deleteSolution(id: Long)

    fun recommendSolution(solutionId: Long, userId: String)

    fun adoptSolution(solutionId: Long, email: String): Boolean

}
