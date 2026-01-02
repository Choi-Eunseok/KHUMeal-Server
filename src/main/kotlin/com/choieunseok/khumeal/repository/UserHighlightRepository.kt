package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.UserHighlightEntity
import com.choieunseok.khumeal.model.entity.UserHighlightId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserHighlightRepository : JpaRepository<UserHighlightEntity, UserHighlightId> {

    @Query("""
        SELECT h
        FROM UserHighlightEntity h
        WHERE h.id.userId = :userId
        AND h.id.menuItemUuid
        IN :uuids
""")
    fun findAllByUserIdAndMenuItemUuids(userId: String, uuids: List<UUID>): List<UserHighlightEntity>

}