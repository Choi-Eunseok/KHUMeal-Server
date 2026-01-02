package com.choieunseok.khumeal.service

import com.choieunseok.khumeal.model.dto.UserHighlightRequest
import com.choieunseok.khumeal.model.dto.UserHighlightResponse
import com.choieunseok.khumeal.model.entity.UserHighlightEntity
import com.choieunseok.khumeal.model.entity.UserHighlightId
import com.choieunseok.khumeal.repository.MenuItemRepository
import com.choieunseok.khumeal.repository.UserHighlightRepository
import com.choieunseok.khumeal.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserHighlightService(
    private val userHighlightRepository: UserHighlightRepository,
    private val userRepository: UserRepository,
    private val menuItemRepository: MenuItemRepository
) {

    @Transactional
    fun saveHighlight(request: UserHighlightRequest) {
        val id = UserHighlightId(
            userId = request.userId,
            menuItemUuid = UUID.fromString(request.menuItemUuid)
        )

        if (request.isHighlighted) {
            val user = userRepository.getReferenceById(request.userId)
            val menuItem = menuItemRepository.getReferenceById(id.menuItemUuid)
            userHighlightRepository.save(UserHighlightEntity(id, user, menuItem))
        } else {
            userHighlightRepository.deleteById(id)
        }
    }

    @Transactional(readOnly = true)
    fun getHighlightsForUser(userId: String, menuUuids: List<String>): UserHighlightResponse {
        val uuidList = menuUuids.map { UUID.fromString(it) }
        val entities = userHighlightRepository.findAllByUserIdAndMenuItemUuids(userId, uuidList)

        return UserHighlightResponse(
            highlightedUuids = entities.map { it.id.menuItemUuid.toString() }
        )
    }

}