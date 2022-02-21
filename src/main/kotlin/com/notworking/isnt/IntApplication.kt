package com.notworking.isnt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.*
import javax.annotation.PostConstruct


@SpringBootApplication
@EnableScheduling
class IntApplication

fun main(args: Array<String>) {
    runApplication<IntApplication>(*args)
}

@PostConstruct
fun started() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
}
