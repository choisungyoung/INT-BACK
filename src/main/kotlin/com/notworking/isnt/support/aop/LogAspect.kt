package com.notworking.isnt.support.aop

import com.notworking.isnt.model.ImageLog
import com.notworking.isnt.service.ImageLogService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


@Aspect
@Component
class LogAspect(
    var imageLogService: ImageLogService
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val MAX_LENGTH = 250

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    @Throws(Throwable::class)
    fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
        val request =
            (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        var start: Long = 0
        var executionTime: Long = 0
        var proceed: Object? = null
        var saveImageLog: ImageLog? = null
        try {
            log.debug(
                "▶▶ START CONTROLLER: {}.{}({})",
                joinPoint.target.javaClass,
                joinPoint.staticPart.signature.name,
                buildMethodArguments(joinPoint)
            )

            saveImageLog = imageLogService.saveImageLog(
                ImageLog(
                    id = null,
                    userId = "",
                    className = joinPoint.target.javaClass.toString(),
                    methodName = joinPoint.staticPart.signature.name,
                    requestIp = request.remoteHost,
                    requestUrl = request.requestURI,
                    requestMethod = request.method,
                    requestBody = "",
                    requestHeader = "",
                    requestParam = request.queryString,
                )
            )

            start = System.currentTimeMillis()
            proceed = joinPoint.proceed() as Object
            executionTime = System.currentTimeMillis() - start

            log.info("${joinPoint.signature} excuted in ${executionTime}ms")
            return proceed
        } catch (e: Exception) {
            val response =
                (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).response

            if (saveImageLog != null && response != null) {
                saveImageLog.responseStatus = response.status.toString()

                if (e.stackTraceToString().length < MAX_LENGTH)
                    saveImageLog.errorStack = e.stackTraceToString()
                else {
                    saveImageLog.errorStack = e.stackTraceToString().substring(0, MAX_LENGTH)
                }

                imageLogService.updateImageLog(saveImageLog)
            }
            return proceed
        } finally {
            log.debug(
                "▶▶ END CONTROLLER RESULT : {})", proceed
            )

            if (saveImageLog != null && proceed != null) {
                var responseEntity = proceed as ResponseEntity<*>

                saveImageLog.responseStatus = responseEntity.statusCode.toString()
                saveImageLog.responseHeader = responseEntity.headers.toString()
                saveImageLog.executionTime = executionTime
                if (responseEntity.body.toString().length < MAX_LENGTH)
                    saveImageLog.responseBody = responseEntity.body.toString()
                else {
                    saveImageLog.responseBody = responseEntity.body.toString().substring(0, MAX_LENGTH)
                }

                imageLogService.updateImageLog(saveImageLog)
            }
        }
    }

    private fun buildMethodArguments(joinPoint: ProceedingJoinPoint): String? {
        val args = joinPoint.args
        val lst: MutableList<String> = ArrayList(args.size)
        var strParam = ""
        for (arg in args) {
            strParam += arg
        }

        return strParam
    }
}