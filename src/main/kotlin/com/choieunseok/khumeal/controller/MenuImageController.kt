package com.choieunseok.khumeal.controller

import com.choieunseok.khumeal.repository.CornerMenuRepository
import com.choieunseok.khumeal.service.MenuGrpcClient
import org.slf4j.LoggerFactory
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.NoSuchElementException

@RestController
@RequestMapping("/api/")
class MenuImageController(
    private val cornerMenuRepository: CornerMenuRepository,
    private val grpcClient: MenuGrpcClient
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/image/{uuid}")
    @Transactional
    fun getMenuImage(@PathVariable uuid: UUID): ResponseEntity<ByteArray> {
        val cornerMenu = cornerMenuRepository.findById(uuid)
            .orElseThrow { NoSuchElementException("이미지를 찾을 수 없습니다.") }

        cornerMenu.image?.let { return imageResponse(cornerMenu.cornerMenuUuid, it) }

        val placeholderText = cornerMenu.menuItems
            .sortedBy { it.itemIndex }
            .joinToString("\n") { it.itemName }

        val generated = try {
            grpcClient.generatePlaceholder(placeholderText)
        } catch (e: Exception) {
            log.warn("placeholder 생성 실패(미저장, 다음 요청에 재시도): uuid=$uuid, ${e.message}")
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).build()
        }

        cornerMenu.image = generated
        cornerMenuRepository.save(cornerMenu)
        return imageResponse(cornerMenu.cornerMenuUuid, generated)
    }

    private fun imageResponse(uuid: UUID, image: ByteArray): ResponseEntity<ByteArray> =
        ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)) // 24시간 동안 클라이언트 캐싱
            .eTag(uuid.toString())
            .contentType(imageMediaType(image))
            .body(image)

    private fun imageMediaType(bytes: ByteArray): MediaType =
        if (bytes.size >= 4 &&
            bytes[0] == 0x89.toByte() && bytes[1] == 0x50.toByte() &&
            bytes[2] == 0x4E.toByte() && bytes[3] == 0x47.toByte()
        ) MediaType.IMAGE_PNG else MediaType.IMAGE_JPEG
}
