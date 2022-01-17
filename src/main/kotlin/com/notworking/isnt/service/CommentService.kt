package com.notworking.isnt.service

import com.notworking.isnt.model.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CommentService {

    fun findAllComment(): List<Comment>

    fun findAllComment(pageable: Pageable, solutionId: Long): Page<Comment>

    fun findComment(id: Long): Comment?

    fun saveComment(comment: Comment, userId: String, solutionId: Long): Comment

    fun updateComment(comment: Comment)

    fun deleteComment(id: Long)
}
