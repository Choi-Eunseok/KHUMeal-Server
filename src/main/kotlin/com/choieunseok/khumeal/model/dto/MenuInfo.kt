package com.choieunseok.khumeal.model.dto

data class MenuInfo(
    val menuInfoUuid: String,
    val cornerName: String,
    val items: List<MenuItem>,
    val imageUrl: String?
)