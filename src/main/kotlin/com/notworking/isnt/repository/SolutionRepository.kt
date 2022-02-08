package com.notworking.isnt.repository

import com.notworking.isnt.model.Developer
import com.notworking.isnt.model.Solution
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SolutionRepository : JpaRepository<Solution, Long> {
    fun findAllByDeveloper(developer: Developer): List<Solution>
}