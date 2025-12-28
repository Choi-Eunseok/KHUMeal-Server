package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.UsersEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository : JpaRepository<UsersEntity, String> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO users (user_id, fcm_token, platform, last_login, created_at)
        VALUES (:userId, :fcmToken, :platform, NOW(), NOW())
        ON CONFLICT (user_id) DO UPDATE SET
            fcm_token = EXCLUDED.fcm_token,
            last_login = NOW()
    """, nativeQuery = true)
    fun upsertUser(
        @Param("userId") userId: String,
        @Param("fcmToken") fcmToken: String,
        @Param("platform") platform: String
    )

}