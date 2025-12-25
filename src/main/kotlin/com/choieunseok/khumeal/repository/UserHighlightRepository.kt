package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.UserHighlightEntity
import com.choieunseok.khumeal.model.entity.UserHighlightId
import com.choieunseok.khumeal.model.entity.UsersEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserHighlightRepository : JpaRepository<UserHighlightEntity, UserHighlightId> {
    // 특정 사용자의 모든 하이라이트 목록 가져오기
    fun findAllByUser(user: UsersEntity): List<UserHighlightEntity>
}