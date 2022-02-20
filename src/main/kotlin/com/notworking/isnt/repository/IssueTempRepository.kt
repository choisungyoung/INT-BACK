package com.notworking.isnt.repository

import com.notworking.isnt.model.Developer
import com.notworking.isnt.model.IssueTemp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IssueTempRepository : JpaRepository<IssueTemp, Long> {
    fun findByDeveloperUserId(developerUserId: String): IssueTemp?
    fun deleteAllByDeveloper(developer: Developer)
}