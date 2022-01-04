package com.notworking.isnt.service

import com.notworking.isnt.model.Comment
import com.notworking.isnt.support.type.PostType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CommentService {

    fun findAllComment(): List<Comment>

    fun findAllComment(pageable: Pageable): Page<Comment>

    fun findComment(id: Long): Comment?

    fun saveComment(comment: Comment, email: String, postId: Long, postType: PostType): Comment

    fun updateComment(comment: Comment)

    fun deleteComment(id: Long)
}
