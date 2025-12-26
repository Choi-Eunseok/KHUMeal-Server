package com.choieunseok.khumeal.controller

import com.choieunseok.khumeal.repository.MenuInfoRepository
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.NoSuchElementException

@RestController
@RequestMapping("/api/")
class MenuImageController(private val menuInfoRepository: MenuInfoRepository) {

    @GetMapping("/image/{uuid}", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getMenuImage(@PathVariable uuid: UUID): ResponseEntity<ByteArray> {
        val menuInfo = menuInfoRepository.findById(uuid)
            .orElseThrow { NoSuchElementException("이미지를 찾을 수 없습니다.") }

        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)) // 24시간 동안 클라이언트 캐싱
            .eTag(menuInfo.menuInfoUuid.toString())
            .contentType(MediaType.IMAGE_JPEG)
            .body(menuInfo.image)
    }

}