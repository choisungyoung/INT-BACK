package com.notworking.isnt.service.impl

import com.notworking.isnt.model.Comment
import com.notworking.isnt.repository.CommentRepository
import com.notworking.isnt.service.CommentService
import com.notworking.isnt.service.DeveloperService
import com.notworking.isnt.service.IssueService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import com.notworking.isnt.support.type.PostType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CommentServiceImpl(

    val commentRepository: CommentRepository,
    val issueService: IssueService,
    val developerService: DeveloperService

) : CommentService {


    override fun findAllComment(): List<Comment> {
        return commentRepository.findAll()
    }

    override fun findAllComment(pageable: Pageable): Page<Comment> {
        return commentRepository.findAll(pageable)
    }

    override fun findComment(id: Long): Comment? {
        return commentRepository.findById(id).orElseThrow { BusinessException(Error.COMMENT_NOT_FOUND) }
    }

    override fun saveComment(comment: Comment, email: String, postId: Long, postType: PostType): Comment {
        var developer = developerService.findDeveloperByEmail(email)
        // 없는 작성자일 경우
        developer ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)
        comment.developer = developer   // 댓글 작성자 정보 추가

        if (PostType.ISSUE == postType) {
            var issue = issueService.findIssue(postId)
            issue ?: throw BusinessException(Error.ISSUE_NOT_FOUND) // 없는 이슈일 경우
            issue.comments.add(comment)
        }
        
        return comment
    }

    override fun updateComment(newComment: Comment) {
        var comment: Comment? = newComment.id?.let {
            commentRepository.getById(it)
        }

        comment ?: throw BusinessException(Error.ISSUE_NOT_FOUND)
        comment.update(newComment)
    }

    override fun deleteComment(id: Long) {
        commentRepository.deleteById(id)
    }
}