package com.noom.interview.fullstack.sleep

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SleepKotlinApplication {
	companion object {
		const val UNIT_TEST_PROFILE = "unittest"
	}
}

fun main(args: Array<String>) {
	runApplication<SleepKotlinApplication>(*args)
}
