package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Comment
import com.notworking.isnt.repository.CommentRepository
import com.notworking.isnt.service.CommentService
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.SolutionService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentServiceImpl(

    val commentRepository: CommentRepository,
    val solutionService: SolutionService,
    val developerService: DeveloperService

) : CommentService {

    override fun findAllComment(): List<Comment> {
        return commentRepository.findAll()
    }

    override fun findAllComment(pageable: Pageable, solutionId: Long): Page<Comment> {
        return commentRepository.findAllBySolutionId(pageable, solutionId)
    }

    override fun findComment(id: Long): Comment? {
        return commentRepository.findById(id).orElseThrow { BusinessException(Error.COMMENT_NOT_FOUND) }
    }

    @Transactional
    override fun saveComment(comment: Comment, email: String, solutionId: Long): Comment {


        var developer = developerService.findDeveloperByEmail(email)
        // 없는 작성자일 경우
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        // 솔루션 조회
        var solution = solutionService.findSolution(solutionId)
        solution ?: throw BusinessException(Error.SOLUTION_NOT_FOUND) // 없는 솔루션일 경우

        comment.developer = developer   // 댓글 작성자 정보 추가
        comment.solution = solution
        
        commentRepository.save(comment)
        return comment
    }

    @Transactional
    override fun updateComment(newComment: Comment) {

        var comment: Comment? = newComment.id?.let {
            commentRepository.getById(it)
        }

        comment ?: throw BusinessException(Error.ISSUE_NOT_FOUND)
        comment.update(newComment)
    }

    @Transactional
    override fun deleteComment(id: Long) {
        commentRepository.deleteById(id)
    }
}