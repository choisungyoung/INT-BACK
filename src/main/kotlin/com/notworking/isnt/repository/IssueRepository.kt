package com.notworking.isnt.repository

import com.notworking.isnt.model.Issue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IssueRepository : JpaRepository<Issue, Long> {
}