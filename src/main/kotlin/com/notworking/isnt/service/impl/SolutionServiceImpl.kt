package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Recommend
import com.notworking.isnt.model.Solution
import com.notworking.isnt.repository.CommentRepository
import com.notworking.isnt.repository.IssueRepository
import com.notworking.isnt.repository.RecommendRepository
import com.notworking.isnt.repository.SolutionRepository
import com.notworking.isnt.repository.support.SolutionRepositorySupport
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.SolutionService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SolutionServiceImpl(

    val developerService: DeveloperService,
    val issueRepository: IssueRepository,
    val solutionRepository: SolutionRepository,
    val solutionRepositorySupport: SolutionRepositorySupport,
    val commentRepository: CommentRepository,
    val recommendRepository: RecommendRepository,

    ) :
    SolutionService {

    override fun findSolution(id: Long): Solution {
        //솔루션 조회
        var solution = solutionRepository.findById(id).orElseThrow { BusinessException(Error.ISSUE_NOT_FOUND) }

        // 댓글 조회
        solution.comments = solutionRepositorySupport.findCommentBySolutionId(id)

        return solution
    }

    override fun findAllSolution(): List<Solution> {
        return solutionRepository.findAll()
    }

    override fun findAllSolution(pageable: Pageable, issueId: Long): Page<Solution> {
        return solutionRepositorySupport.findSolutionByIssueId(pageable, issueId)
    }

    override fun findAllSolutionWithComment(pageable: Pageable, issueId: Long): Page<Solution> {
        var page: Page<Solution> = solutionRepositorySupport.findSolutionByIssueId(pageable, issueId);

        for (solution in page.content) {
            solution.comments = commentRepository.findAllBySolutionId(solutionId = solution.id!!)
        }

        return PageImpl<Solution>(page.content, pageable, page.totalElements)
    }

    override fun findSolutionCount(issueId: Long): Long {
        return solutionRepositorySupport.findSolutionCount(issueId)
    }

    override fun findSolutionAdoptYn(issueId: Long): Boolean {
        if (solutionRepositorySupport.findSolutionAdopt(issueId) == 0L) {
            return false
        }
        return true
    }

    override fun findAllSolution(issueId: Long): List<Solution> {
        return solutionRepositorySupport.findSolutionByIssueId(issueId)
    }

    @Transactional
    override fun saveSolution(solution: Solution, userId: String, issueId: Long): Solution {
        // 사용자 조회
        var developer = developerService.findDeveloperByUserId(userId)
        // 없는 작성자일 경우
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        // 이슈 조회
        var issue = issueRepository.findById(issueId).get()
        solution.developer = developer
        solution.issue = issue
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
        solution.adoptYn = false    // 수정하면 언채택되도록
    }

    @Transactional
    override fun deleteSolution(id: Long) {
        var solution = solutionRepository.findById(id).orElseThrow {
            throw BusinessException(Error.SOLUTION_NOT_FOUND)
        }

        //코멘트 조회
        solution.comments = commentRepository.findAllBySolutionId(id)
        solutionRepository.delete(solution) //코멘트도 전의되어 삭제
    }

    @Transactional
    override fun recommendSolution(solutionId: Long, userId: String) {

        var solution = solutionRepository.findById(solutionId).orElseThrow {
            throw BusinessException(Error.SOLUTION_NOT_FOUND)
        }
        var developer =
            developerService.findDeveloperByUserId(userId) ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        // 이미 추천한 솔루션인지 체크
        var recommend = recommendRepository.findAllBySolutionAndDeveloper(solution, developer)

        if (recommend == null) {
            // 이미 추천한 이력이 없는 경우 새로 추가
            recommend = Recommend(null)
            solution?.recommendationCount = solution?.recommendationCount!!.plus(1)

            recommend.solution = solution
            recommend.developer = developer
            recommendRepository.save(recommend)
        } else {
            // 이미 추천한 이력이 있는 경우 마이너스
            solution?.recommendationCount =
                solution?.recommendationCount!!.plus(-1)
            recommendRepository.delete(recommend)
        }


    }

    @Transactional
    override fun adoptSolution(solutionId: Long, userId: String): Boolean {

        var solution = solutionRepository.findById(solutionId).orElseThrow {
            throw BusinessException(Error.SOLUTION_NOT_FOUND)
        }
        if (solution.developer.userId != userId) {
            throw BusinessException(Error.SOLUTION_NOT_DEVELOPER)
        }
        solution.issue ?: throw BusinessException(Error.ISSUE_NOT_FOUND)

        // 이미채택된 솔루션 채택 취소
        solutionRepository.findAllByIssueAndAdoptYn(solution.issue!!, true).forEach {
            it.adoptYn = false
        }

        // 채택 토글
        solution.adoptYn = !solution.adoptYn

        return solution.adoptYn
    }
}