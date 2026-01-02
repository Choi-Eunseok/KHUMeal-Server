package com.choieunseok.khumeal.model.dto

data class MenuResponse(
    val restaurantName: String,
    val date: String,
    val menuItems: List<MenuInfo>
)