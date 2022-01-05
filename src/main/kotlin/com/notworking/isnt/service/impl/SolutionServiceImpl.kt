package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Solution
import com.notworking.isnt.repository.SolutionRepository
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.service.SolutionService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SolutionServiceImpl(

    val developerService: DeveloperService,
    val issueService: IssueService,
    val solutionRepository: SolutionRepository,

    ) :
    SolutionService {

    override fun findAllSolution(): List<Solution> {
        return solutionRepository.findAll()
    }

    override fun findAllSolution(pageable: Pageable, issueId: Long): Page<Solution> {
        return solutionRepository.findAll(pageable)
    }

    @Transactional
    override fun saveSolution(solution: Solution, email: String, issueId: Long): Solution {
        // 사용자 조회
        var developer = developerService.findDeveloperByEmail(email)
        // 없는 작성자일 경우
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        // 이슈 조회
        var issue = issueService.findIssue(issueId)
        // 없는 이슈일 경우
        issue ?: throw BusinessException(Error.ISSUE_NOT_FOUND)

        solution.developer = developer
        solution.updateIssue(issue)

        solutionRepository.save(solution)
        return solution
    }

    @Transactional
    override fun updateSolution(newSolution: Solution) {
        var solution: Solution? = newSolution.id?.let {
            solutionRepository.getById(it)
        }

        solution ?: throw BusinessException(Error.SOLUTION_NOT_FOUND)
        solution.update(newSolution)
    }

    @Transactional
    override fun deleteSolution(id: Long) {
        solutionRepository.deleteById(id)
    }
}