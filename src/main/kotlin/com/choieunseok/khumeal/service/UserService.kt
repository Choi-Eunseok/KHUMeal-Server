package com.choieunseok.khumeal.service

import com.choieunseok.khumeal.model.dto.UserSyncRequest
import com.choieunseok.khumeal.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun syncUser(request: UserSyncRequest) {
        userRepository.upsertUser(
            userId = request.user_id,
            fcmToken = request.fcm_token,
            platform = request.platform
        )
    }

}