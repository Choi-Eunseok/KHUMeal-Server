package com.choieunseok.khumeal.model.dto

data class UserHighlightRequest(
    val userId: String,
    val menuItemUuid: String,
    val isHighlighted: Boolean
)