package com.notworking.isnt.service.impl

import com.notworking.isnt.model.ImageLog
import com.notworking.isnt.repository.ImageLogRepository
import com.notworking.isnt.service.ImageLogService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ImageLogServiceImpl(
    val imageLogRepository: ImageLogRepository
) : ImageLogService {
    @Transactional
    override fun saveImageLog(imageLog: ImageLog): ImageLog {
        return imageLogRepository.save(imageLog)
    }

    @Transactional
    override fun updateImageLog(imageLog: ImageLog) {
        imageLog.id?.let {
            var selImageLog = imageLogRepository.findById(it).get()
            selImageLog.responseStatus = imageLog.responseStatus
            selImageLog.responseBody = imageLog.responseBody
            selImageLog.responseHeader = imageLog.responseHeader
            selImageLog.errorStack = imageLog.errorStack
            selImageLog.executionTime = imageLog.executionTime
        }
    }

}