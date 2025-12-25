package com.choieunseok.khumeal.controller

import com.choieunseok.khumeal.service.MenuSyncService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/menu")
class MenuAdminController(
    private val menuSyncService: MenuSyncService
) {

    @PostMapping("/sync/all")
    fun syncAll(): ResponseEntity<Map<String, String>> {
        return try {
            menuSyncService.syncAllMenus()
            ResponseEntity.ok(mapOf("status" to "SUCCESS", "message" to "모든 식당 동기화 작업이 완료되었습니다."))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("status" to "ERROR", "message" to (e.message ?: "알 수 없는 오류 발생")))
        }
    }

    @PostMapping("/sync/{nameId}")
    fun syncOne(@PathVariable nameId: Int): ResponseEntity<Map<String, String>> {
        return try {
            menuSyncService.syncLatestMenu(nameId)
            ResponseEntity.ok(mapOf("status" to "SUCCESS", "message" to "ID: $nameId 식당 동기화가 완료되었습니다."))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("status" to "ERROR", "message" to (e.message ?: "알 수 없는 오류 발생")))
        }
    }
}