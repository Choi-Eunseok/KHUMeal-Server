package com.choieunseok.khumeal.service

import com.choieunseok.khumeal.model.dto.HighlightRequest
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
    fun saveHighlight(request: HighlightRequest) {
        val id = UserHighlightId(
            userId = request.userId,
            menuItemUuid = UUID.fromString(request.menuInfoUuid),
            menuIndex = request.menuIndex
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
    fun getHighlightsForUser(userId: String, menuUuids: List<String>): List<UserHighlightResponse> {
        val uuidList = menuUuids.map { UUID.fromString(it) }

        val entities = userHighlightRepository.findAllByUserIdAndMenuItemUuids(userId, uuidList)

        return entities
            .groupBy { it.id.menuItemUuid }
            .map { (uuid, highlights) ->
                UserHighlightResponse(
                    menuInfoUuid = uuid.toString(),
                    highlightedIndices = highlights.map { it.id.menuIndex }
                )
            }
    }

}