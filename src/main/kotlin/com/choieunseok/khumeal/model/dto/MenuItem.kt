package com.choieunseok.khumeal.model.dto

data class MenuItem(
    val menuInfoUuid: String,
    val cornerName: String,
    val items: List<String>,
    val imageUrl: String?
)