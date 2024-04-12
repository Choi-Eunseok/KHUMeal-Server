package com.choieunseok.khumeal

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class ManagerApplication : SpringBootServletInitializer() {

    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(ManagerApplication::class.java)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(ManagerApplication::class.java, *args)
}