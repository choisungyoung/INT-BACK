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

    fun saveSolution(solution: Solution, email: String, issueId: Long): Solution

    fun updateSolution(solution: Solution)

    fun deleteSolution(id: Long)
}
