package com.notworking.isnt.repository

import com.notworking.isnt.model.Developer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeveloperRepository : JpaRepository<Developer, Long> {
    fun findByName(name: String?): Developer?

    fun findByUserId(userId: String?): Developer?

    fun existsByUserId(userId: String?): Boolean

    fun existsByName(userId: String?): Boolean
}