package com.choieunseok.khumeal.model.dto

data class UserSyncRequest(
    val user_id: String,
    val fcm_token: String,
    val platform: String
)