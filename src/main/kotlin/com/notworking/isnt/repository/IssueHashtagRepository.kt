package com.notworking.isnt.repository

import com.notworking.isnt.model.IssueHashtag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IssueHashtagRepository : JpaRepository<IssueHashtag, Long> {
    fun findAllByIssueId(issueId: Long?): MutableList<IssueHashtag>
    fun countByHashtagId(hashtagId: Long?): Int
}