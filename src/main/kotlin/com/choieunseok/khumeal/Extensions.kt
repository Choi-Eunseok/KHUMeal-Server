package com.choieunseok.khumeal

import com.choieunseok.khumeal.model.entity.MenuInfoEntity
import java.time.LocalDate

fun getWeekRange(baseDate: LocalDate): Pair<LocalDate, LocalDate> {
    val dayOfWeek = baseDate.dayOfWeek.value % 7 // 일=0, 월=1 ... 토=6
    val saturday = if (dayOfWeek == 6) baseDate else baseDate.minusDays(dayOfWeek + 1L)
    val friday = saturday.plusDays(6)
    return Pair(saturday, friday)
}

private val INVALID_CORNER_KEYWORDS = setOf("One", "교직원 비빔코너", "학생식당 중식 Self")

fun MenuInfoEntity.hasValidCorner(): Boolean {
    val name = this.cornerInfo.trim()

    return when {
        name.isBlank() -> false
        name.length < 2 -> false
        INVALID_CORNER_KEYWORDS.any { name == it } -> false
        else -> true
    }
}