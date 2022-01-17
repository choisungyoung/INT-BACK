package com.notworking.isnt.support.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Aspect
@Component
class LogAspect {
    private val log = LoggerFactory.getLogger(javaClass)

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    @Throws(Throwable::class)
    fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
        var start: Long
        var executionTime: Long
        var proceed: Object? = null
        
        try {
            log.debug(
                "▶▶ START CONTROLLER: {}.{}({})",
                joinPoint.target.javaClass,
                joinPoint.staticPart.signature.name,
                buildMethodArguments(joinPoint)
            )

            start = System.currentTimeMillis()
            proceed = joinPoint.proceed() as Object
            executionTime = System.currentTimeMillis() - start

            log.info("${joinPoint.signature} excuted in ${executionTime}ms")
            return proceed
        } finally {
            log.debug(
                "▶▶ END CONTROLLER RESULT : {})", proceed
            )
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