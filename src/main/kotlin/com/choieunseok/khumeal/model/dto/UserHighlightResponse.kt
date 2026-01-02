package com.choieunseok.khumeal.model.dto

data class UserHighlightResponse(
    val menuInfoUuid: String,
    val highlightedIndices: List<Int>
)