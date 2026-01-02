package com.choieunseok.khumeal.model.dto

data class HighlightRequest(
    val userId: String,
    val menuInfoUuid: String,
    val menuIndex: Int,
    val isHighlighted: Boolean
)