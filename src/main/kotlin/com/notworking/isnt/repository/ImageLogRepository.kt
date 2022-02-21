package com.notworking.isnt.repository

import com.notworking.isnt.model.ImageLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ImageLogRepository : JpaRepository<ImageLog, Long> {
    fun deleteAllByCreatedDateLessThan(localDateTime: LocalDateTime)
}