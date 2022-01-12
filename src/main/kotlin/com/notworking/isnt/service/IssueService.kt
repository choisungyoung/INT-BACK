package com.notworking.isnt.service

import com.notworking.isnt.model.Issue
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface IssueService {

    fun findAllIssue(): List<Issue>

    fun findAllIssue(pageable: Pageable): Page<Issue>

    fun findAllIssueByTitleContent(pageable: Pageable, query: String?): Page<Issue>

    fun findAllLatestOrder(): List<Issue>

    fun findIssue(id: Long): Issue?

    fun saveIssue(issue: Issue, email: String, hashtags: List<String>): Issue

    fun updateIssue(issue: Issue, hashtags: List<String>)

    fun deleteIssue(id: Long)
}
