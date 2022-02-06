package com.notworking.isnt.service

import com.notworking.isnt.model.ImageLog

interface ImageLogService {

    fun saveImageLog(imageLog: ImageLog): ImageLog

    fun updateImageLog(imageLog: ImageLog)
}
