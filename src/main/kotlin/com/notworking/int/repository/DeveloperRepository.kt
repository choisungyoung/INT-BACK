package com.notworking.int.repository

import com.notworking.int.model.Developer
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DeveloperRepository : CrudRepository<Developer, Long>{
    fun findByEmail(email: String?): Optional<Developer?>
}