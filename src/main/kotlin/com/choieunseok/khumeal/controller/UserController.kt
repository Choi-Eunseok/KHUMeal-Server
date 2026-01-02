package com.choieunseok.khumeal.controller

import com.choieunseok.khumeal.model.dto.UserHighlightRequest
import com.choieunseok.khumeal.model.dto.UserHighlightResponse
import com.choieunseok.khumeal.model.dto.UserSyncRequest
import com.choieunseok.khumeal.service.UserHighlightService
import com.choieunseok.khumeal.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
    private val userHighlightService: UserHighlightService
) {

    @PostMapping("/sync")
    fun syncUser(@RequestBody request: UserSyncRequest): ResponseEntity<String> {
        userService.syncUser(request)
        return ResponseEntity.ok("User synced successfully")
    }

    @PostMapping("/highlight")
    fun updateHighlight(@RequestBody request: UserHighlightRequest): ResponseEntity<String> {
        userHighlightService.saveHighlight(request)
        return ResponseEntity.ok("Highlight updated")
    }

    @GetMapping("/{userId}/highlights")
    fun getHighlights(
        @PathVariable userId: String,
        @RequestParam menuUuids: List<String>
    ): ResponseEntity<UserHighlightResponse> {
        val results = userHighlightService.getHighlightsForUser(userId, menuUuids)
        return ResponseEntity.ok(results)
    }

}