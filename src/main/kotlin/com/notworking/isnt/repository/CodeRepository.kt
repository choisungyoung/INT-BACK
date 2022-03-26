package com.notworking.isnt.repository

import com.notworking.isnt.model.Code
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CodeRepository : JpaRepository<Code, Long> {
    fun findByCode(code: String): List<Code>
}