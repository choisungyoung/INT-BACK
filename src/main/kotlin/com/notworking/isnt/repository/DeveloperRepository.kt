package com.notworking.isnt.repository

import com.notworking.isnt.model.Developer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeveloperRepository : JpaRepository<Developer, Long> {
    fun findByEmail(email: String?): Developer?
}