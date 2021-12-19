package com.notworking.int

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IntApplication

fun main(args: Array<String>) {
	runApplication<IntApplication>(*args)
}
