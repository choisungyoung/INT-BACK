package com.notworking.isnt.service

import com.notworking.isnt.model.Solution
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SolutionService {

    fun findSolution(id: Long): Solution?

    fun findAllSolution(): List<Solution>

    fun findAllSolutionWithComment(pageable: Pageable, issueId: Long): Page<Solution>

    fun findAllSolution(pageable: Pageable, issueId: Long): Page<Solution>

    fun findAllSolution(issueId: Long): List<Solution>

    fun findSolutionCount(issueId: Long): Long

    fun findSolutionAdoptYn(issueId: Long): Boolean

    fun saveSolution(solution: Solution, userId: String, issueId: Long): Solution

    fun updateSolution(solution: Solution)

    fun deleteSolution(id: Long)

    fun recommendSolution(solutionId: Long, userId: String, recommendYn: Boolean)

    fun adoptSolution(solutionId: Long, userId: String): Boolean

}
