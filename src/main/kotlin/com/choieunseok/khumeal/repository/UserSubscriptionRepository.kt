package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.UserSubscriptionEntity
import com.choieunseok.khumeal.model.entity.UserSubscriptionId
import com.choieunseok.khumeal.model.entity.UsersEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserSubscriptionRepository : JpaRepository<UserSubscriptionEntity, UserSubscriptionId> {
    // 특정 사용자의 구독 정보 찾기
    fun findAllByUser(user: UsersEntity): List<UserSubscriptionEntity>
}