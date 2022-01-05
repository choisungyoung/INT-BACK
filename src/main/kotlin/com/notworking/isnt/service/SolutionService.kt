package com.notworking.isnt.service

import com.notworking.isnt.model.Solution
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SolutionService {

    fun findAllSolution(): List<Solution>

    fun findAllSolution(pageable: Pageable, issueId: Long): Page<Solution>

    fun saveSolution(solution: Solution, email: String, issueId: Long): Solution

    fun updateSolution(solution: Solution)

    fun deleteSolution(id: Long)
}
