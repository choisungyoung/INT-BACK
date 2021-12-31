package com.notworking.isnt.repository

import com.notworking.isnt.model.Developer
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeveloperRepository : CrudRepository<Developer, Long> {
    fun findByEmail(email: String?): Developer?
}