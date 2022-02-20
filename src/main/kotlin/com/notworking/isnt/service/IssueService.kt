package com.notworking.isnt.service

import com.notworking.isnt.model.Issue
import com.notworking.isnt.model.IssueTemp
import com.querydsl.core.Tuple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface IssueService {

    fun findAllIssue(): List<Issue>

    fun findAllIssue(pageable: Pageable): Page<Issue>

    fun findAllIssue(pageable: Pageable, query: String?): Page<Tuple>

    fun findAllLatestOrder(): List<Issue>

    fun findIssue(id: Long): Issue?

    fun findIssueTemp(userId: String): IssueTemp?

    fun saveIssue(issue: Issue, userId: String, hashtags: List<String>?): Issue

    fun saveIssueTemp(issueTemp: IssueTemp, userId: String): IssueTemp?

    fun updateIssue(issue: Issue, hashtags: List<String>?)

    fun deleteIssue(id: Long)
}
