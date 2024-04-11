package com.choieunseok.khumeal.controller

import com.choieunseok.khumeal.service.SecondDormitoryService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/sec-dorm")
class SecondDormitoryController(
    val service: SecondDormitoryService,
    val logger: Logger
) {

    @GetMapping(path = ["ping", "ping/"])
    fun hello(): String {
        return "pong"
    }

    @GetMapping(path = ["", "/"])
    fun get(): JsonNode {
        val data = service.getSecondDormitoryModel()

        val mapper = ObjectMapper()
        logger.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data))

        return mapper.valueToTree(data)
    }
}