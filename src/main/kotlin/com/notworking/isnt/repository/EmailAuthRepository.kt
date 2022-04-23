package com.notworking.isnt.repository

import com.notworking.isnt.model.EmailAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface EmailAuthRepository : JpaRepository<EmailAuth, Long> {
    fun findByEmailAndExpiredDateGreaterThanEqual(email: String, nowDate: LocalDateTime): EmailAuth?

    fun findByEmail(email: String): EmailAuth?
}