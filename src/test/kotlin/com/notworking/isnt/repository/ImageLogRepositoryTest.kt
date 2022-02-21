package com.notworking.isnt.repository

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Disabled
@SpringBootTest
@Transactional
internal class ImageLogRepositoryTest(
    @Autowired var imageLogRepository: ImageLogRepository
) {

    @Test
    @Rollback(false)
    fun testDeleteImageLog() {
        imageLogRepository.deleteAllByCreatedDateLessThan(LocalDateTime.now().minusDays(90))
    }

}