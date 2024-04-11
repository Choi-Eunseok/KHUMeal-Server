package com.choieunseok.khumeal

import com.fasterxml.jackson.databind.JsonNode
import java.text.SimpleDateFormat
import java.util.*

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun Date.getDayOfWeek(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.DAY_OF_WEEK)
}

fun Date.isThisWeek(): Boolean {
    val calendar = Calendar.getInstance()
//    calendar.add(Calendar.DATE, -7)

    calendar.add(Calendar.DATE, -(calendar.time.getDayOfWeek() % 6))
    val startDay = calendar.time

    val dateList = mutableListOf<String>()
    var tempDate = startDay
    for (i in 0..6) {
        dateList.add(tempDate.toString("yyyy-MM-dd"))
        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = tempDate
        tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
        tempDate = tempCalendar.time
    }

    return dateList.contains(this.toString("yyyy-MM-dd"))
}

fun String.toDateWithFormat(format: String): Date {
    val formatter = SimpleDateFormat(format)
    return formatter.parse(this)
}

fun JsonNode.toNullableString(): String? {
    if (this.isNull)
        return null
    return this.asText()
}

fun Map<*, *>.toPrettyString(newLine: String, leftPadding: Int = 0): String {
    val sb = StringBuilder()
    for (entry: Map.Entry<*, *> in this.entries) {
        if (entry.value is Map<*, *>) {
            sb.append("%${8}s".format(entry.key))
            (entry.value as Map<*, *>).toPrettyString(newLine, leftPadding + 4)
        } else {
            sb.append(" ".repeat(leftPadding) + "%${8}s : %s$newLine".format(entry.key, entry.value))
        }
    }
    return sb.toString()
}