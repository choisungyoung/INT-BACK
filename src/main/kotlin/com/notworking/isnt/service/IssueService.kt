package com.notworking.isnt.service

import com.notworking.isnt.model.Issue

interface IssueService {

    fun findAllIssue(): List<Issue>

    fun findAllLatestOrder(): List<Issue>

    fun findIssue(id: Long): Issue?

    fun saveIssue(issue: Issue, email: String): Issue

    fun updateIssue(issue: Issue)

    fun deleteIssue(id: Long)
}
