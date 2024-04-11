package com.choieunseok.khumeal.service

import com.choieunseok.khumeal.*
import com.choieunseok.khumeal.model.DayMenuModel
import com.choieunseok.khumeal.model.SecondDormitoryModel
import com.choieunseok.khumeal.model.WeekMenuModel
import com.choieunseok.khumeal.model.WeeklyMenu
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@Service
class SecondDormitoryService : RestTemplateClient() {
    val url = "https://dorm2.khu.ac.kr/food/getWeeklyMenu.kmc"

    fun getSecondDormitoryModel(): SecondDormitoryModel {
        val result = SecondDormitoryModel()
        val c = Calendar.getInstance()
        result.weekMenuList += getWeeklyMenu(c.time)?.toWeekMenuModel()

        c.add(Calendar.DATE, 7)
        result.weekMenuList += getWeeklyMenu(c.time)?.toWeekMenuModel()

        return result
    }

    fun getWeeklyMenu(date: Date): WeeklyMenu? {
        val uri = UriComponentsBuilder
            .fromUriString(url)
            .queryParam("locgbn", "K1")
            .queryParam("fo_gbn", "stu")
            .queryParam("sch_date", date.toString("yyyy-MM-dd"))
            .build()
            .toUri()
        return get(uri)
    }

    // fo_menu_mor{i} : 조식 한식
    // fo_sub_mor{i} : 조식 일품
    // fo_menu_lun{i} : 중식 한식
    // fo_sub_lun{i} : 중식 일품1
    // fo_sub_menu{i} : 중식 일품2
    // fo_menu_eve{i} : 석식 한식
    // fo_sub_eve{i} : 석식 일품
    fun WeeklyMenu.toWeekMenuModel(): WeekMenuModel? {
        if (!this.root.any() || this.root.first().WEEKLYMENU?.any() != true)
            return null

        val array = Array(7) { _ -> DayMenuModel() }
        val mapper = ObjectMapper()

        array.forEachIndexed { index, dayMenuModel ->
            val menuJson = mapper.valueToTree<JsonNode>(this.root.first().WEEKLYMENU?.first()!!)
            dayMenuModel.dateString = menuJson.get("fo_date${index + 1}").toNullableString()
            dayMenuModel.morningMenu = menuJson.get("fo_menu_mor${index + 1}").toNullableString()
            dayMenuModel.morningSubMenu = menuJson.get("fo_sub_mor${index + 1}").toNullableString()
            dayMenuModel.lunchMenu = menuJson.get("fo_menu_lun${index + 1}").toNullableString()
            dayMenuModel.lunchSubFirstMenu = menuJson.get("fo_sub_lun${index + 1}").toNullableString()
            dayMenuModel.lunchSubSecondMenu = menuJson.get("fo_sub_menu${index + 1}").toNullableString()
            dayMenuModel.eveningMenu = menuJson.get("fo_menu_eve${index + 1}").toNullableString()
            dayMenuModel.eveningSubMenu = menuJson.get("fo_sub_eve${index + 1}").toNullableString()
        }

        return WeekMenuModel(array[0], array[1], array[2], array[3], array[4], array[5], array[6])
    }
}