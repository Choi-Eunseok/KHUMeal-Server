package com.choieunseok.khumeal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = ["com.choieunseok.khumeal.model.entity"])
@EnableJpaRepositories(basePackages = ["com.choieunseok.khumeal.repository"])
class KhuMealApplication

fun main(args: Array<String>) {
    runApplication<KhuMealApplication>(*args)
}
