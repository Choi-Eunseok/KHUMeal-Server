package com.choieunseok.khumeal.controller

import com.choieunseok.khumeal.model.dto.UserSyncRequest
import com.choieunseok.khumeal.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userRepository: UserRepository) {

    @PostMapping("/sync")
    fun syncUser(@RequestBody request: UserSyncRequest): ResponseEntity<String> {
        userRepository.upsertUser(
            userId = request.user_id,
            fcmToken = request.fcm_token,
            platform = request.platform
        )
        return ResponseEntity.ok("User synced successfully")
    }
}