package com.choieunseok.khumeal

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import java.net.URI

abstract class RestTemplateClient {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var restTemplate: RestTemplate

    inline fun <reified R> post(url: String, body: Any): R? {
        val httpEntity = HttpEntity(body, HttpHeaders())
        val res = restTemplate.postForEntity(url, httpEntity, R::class.java)
        return objectMapper.convertValue(res.body, R::class.java)
    }

    inline fun <reified R> get(uri: URI): R? {
        val httpEntity = HttpEntity<Any>(HttpHeaders())
        val res = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, R::class.java)
        return res.body
    }
}