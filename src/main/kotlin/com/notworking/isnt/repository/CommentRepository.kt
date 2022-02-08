package com.notworking.isnt.repository

import com.notworking.isnt.model.Comment
import com.notworking.isnt.model.Developer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {

    fun findAllBySolutionId(solutionId: Long): MutableList<Comment>

    fun findAllBySolutionId(pageable: Pageable, solutionId: Long): Page<Comment>

    fun findAllByDeveloper(developer: Developer): List<Comment>
}