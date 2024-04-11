package com.choieunseok.khumeal.model

data class WeeklyMenu(
    val root: List<Root>
)

data class Root(
    val WEEKLYMENU: List<WeeklyMenuDetail>?,
    val LASTNEXT: List<LastNext>?
)

data class LastNext(
    val go: String,
    val mon_date: String? = null
)

// fo_menu_mor{i} : 조식 한식
// fo_sub_mor{i} : 조식 일품
// fo_menu_lun{i} : 중식 한식
// fo_sub_lun{i} : 중식 일품1
// fo_sub_menu{i} : 중식 일품2
// fo_menu_eve{i} : 석식 한식
// fo_sub_eve{i} : 석식 일품
data class WeeklyMenuDetail(
    val locgbn: String,
    val fo_gbn: String,
    val seq: Int,
    val today: String,

    val fo_date1: String,
    val fo_date2: String,
    val fo_date3: String,
    val fo_date4: String,
    val fo_date5: String,
    val fo_date6: String,
    val fo_date7: String,

    val fo_menu_mor1: String?,
    val fo_menu_mor2: String?,
    val fo_menu_mor3: String?,
    val fo_menu_mor4: String?,
    val fo_menu_mor5: String?,
    val fo_menu_mor6: String?,
    val fo_menu_mor7: String?,

    val fo_sub_mor1: String?,
    val fo_sub_mor2: String?,
    val fo_sub_mor3: String?,
    val fo_sub_mor4: String?,
    val fo_sub_mor5: String?,
    val fo_sub_mor6: String?,
    val fo_sub_mor7: String?,

    val fo_menu_lun1: String?,
    val fo_menu_lun2: String?,
    val fo_menu_lun3: String?,
    val fo_menu_lun4: String?,
    val fo_menu_lun5: String?,
    val fo_menu_lun6: String?,
    val fo_menu_lun7: String?,

    val fo_sub_lun1: String?,
    val fo_sub_lun2: String?,
    val fo_sub_lun3: String?,
    val fo_sub_lun4: String?,
    val fo_sub_lun5: String?,
    val fo_sub_lun6: String?,
    val fo_sub_lun7: String?,

    val fo_sub_menu1: String?,
    val fo_sub_menu2: String?,
    val fo_sub_menu3: String?,
    val fo_sub_menu4: String?,
    val fo_sub_menu5: String?,
    val fo_sub_menu6: String?,
    val fo_sub_menu7: String?,

    val fo_menu_eve1: String?,
    val fo_menu_eve2: String?,
    val fo_menu_eve3: String?,
    val fo_menu_eve4: String?,
    val fo_menu_eve5: String?,
    val fo_menu_eve6: String?,
    val fo_menu_eve7: String?,

    val fo_sub_eve1: String?,
    val fo_sub_eve2: String?,
    val fo_sub_eve3: String?,
    val fo_sub_eve4: String?,
    val fo_sub_eve5: String?,
    val fo_sub_eve6: String?,
    val fo_sub_eve7: String?,
)