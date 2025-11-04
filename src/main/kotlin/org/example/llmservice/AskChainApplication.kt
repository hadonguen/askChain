package org.example.llmservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AskChainApplication

fun main(args: Array<String>) {
    runApplication<org.example.llmservice.AskChainApplication>(*args)
}
