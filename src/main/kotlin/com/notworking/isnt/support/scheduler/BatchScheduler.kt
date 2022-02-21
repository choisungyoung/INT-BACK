package com.notworking.isnt.support.scheduler

import com.notworking.isnt.repository.ImageLogRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime


@Component
class BatchScheduler(
    val imageLogRepository: ImageLogRepository
) {
    var logger: Logger = LoggerFactory.getLogger(this.javaClass)

    //매일 5시 로그삭제
    @Scheduled(cron = "0 0 5 * * ?")
    fun deleteLogSchedule() {
        logger.info(">>> [START DELETE LOG] {}")
        imageLogRepository.deleteAllByCreatedDateLessThan(LocalDateTime.now().minusDays(90))
        logger.info(">>> [END DELETE LOG] {}")
    }
}