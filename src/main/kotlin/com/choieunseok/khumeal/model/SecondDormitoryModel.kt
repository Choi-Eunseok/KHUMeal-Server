package com.choieunseok.khumeal.model

data class SecondDormitoryModel(
    var weekMenuList: List<WeekMenuModel?> = emptyList()
)

data class WeekMenuModel(
    val mondayMenu: DayMenuModel,
    val tuesdayMenu: DayMenuModel,
    val wednesdayMenu: DayMenuModel,
    val thursdayMenu: DayMenuModel,
    val fridayMenu: DayMenuModel,
    val saturdayMenu: DayMenuModel,
    val sundayMenu: DayMenuModel
)

data class DayMenuModel(
    var dateString: String? = null,
    var morningMenu: String? = null,
    var morningSubMenu: String? = null,
    var lunchMenu: String? = null,
    var lunchSubFirstMenu: String? = null,
    var lunchSubSecondMenu: String? = null,
    var eveningMenu: String? = null,
    var eveningSubMenu: String? = null
)