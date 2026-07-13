package com.choieunseok.khumeal.service.parser

import com.choieunseok.khumeal.model.entity.MenuSnapshotEntity
import com.choieunseok.khumeal.model.entity.RestaurantEntity
import com.choieunseok.khumeal.model.entity.MenuSourceType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

/**
 * 경희대 기숙사 식당 주간 메뉴 JSON 파서.
 * POST https://dorm2.khu.ac.kr/food/getWeeklyMenu.do (locgbn, sch_date, fo_gbn)
 *
 * 응답: root[0].WEEKLYMENU[] 에 fo_date1~7(월~일), fo_menu_{mor|lun|eve}{1~7}(","로 join된 메뉴).
 * 미운영 요일은 "미운영".
 */
@Component
class DormJsonMenuParser(
    private val objectMapper: ObjectMapper,
    private val restTemplate: RestTemplate
) : MenuSourceParser {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceType = MenuSourceType.DORM_JSON

    data class Config(
        val locgbn: String = "K1",
        val foGbn: String = "stu"
    )

    override fun fetchRecent(restaurant: RestaurantEntity, lastSnapshot: MenuSnapshotEntity?): ParsedWeeklyMenu? {
        val url = restaurant.sourceUrl ?: restaurant.baseUrl
        val config = restaurant.sourceConfig
            ?.let { objectMapper.readValue<Config>(it) }
            ?: Config()

        val today = LocalDate.now()
        // 이번 주 + 다음 주를 함께 가져온다 (다음 주 메뉴가 미리 올라오는 경우 대비)
        val days = (fetchWeek(url, config, today) + fetchWeekOrEmpty(url, config, today.plusDays(7)))
            .distinctBy { it.date }
            .sortedBy { it.date }

        if (days.isEmpty()) return null

        val hash = days.contentHash()
        if (hash == lastSnapshot?.sourceVersion) {
            return null // 내용 변경 없음 → 최신 상태
        }

        return ParsedWeeklyMenu(
            sourceVersion = hash,
            prevSourceVersion = lastSnapshot?.sourceVersion ?: "",
            baseDate = days.first().date,
            imageUrl = "",
            days = days
        )
    }

    private fun fetchWeek(url: String, config: Config, schDate: LocalDate): List<ParsedDailyMenu> {
        val form = LinkedMultiValueMap<String, String>().apply {
            add("locgbn", config.locgbn)
            add("sch_date", schDate.toString())
            add("fo_gbn", config.foGbn)
        }
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED }
        val body = restTemplate.postForObject(url, HttpEntity(form, headers), String::class.java)
            ?: throw IllegalStateException("기숙사 메뉴 응답이 비어 있습니다: $url")
        return parseWeekly(body)
    }

    private fun fetchWeekOrEmpty(url: String, config: Config, schDate: LocalDate): List<ParsedDailyMenu> =
        try {
            fetchWeek(url, config, schDate)
        } catch (e: Exception) {
            log.warn("기숙사 다음 주 메뉴 조회 실패(무시하고 진행): ${e.message}")
            emptyList()
        }

    internal fun parseWeekly(json: String): List<ParsedDailyMenu> {
        val weeklyMenus = objectMapper.readTree(json)
            .path("root").path(0)
            .path("WEEKLYMENU")

        return weeklyMenus.flatMap { week ->
            (1..7).mapNotNull { dayIndex ->
                val dateText = week.path("fo_date$dayIndex").asText("")
                if (dateText.isBlank()) return@mapNotNull null

                val corners = MEAL_FIELDS.mapNotNull { (field, cornerName) ->
                    val raw = week.path("fo_menu_$field$dayIndex").asText("").trim()
                    val items =
                        if (raw.isBlank()) emptyList()
                        else raw.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    if (items.isEmpty()) null else ParsedCorner(cornerName, items)
                }
                ParsedDailyMenu(LocalDate.parse(dateText), corners)
            }
        }
    }

    companion object {
        private val MEAL_FIELDS = listOf("mor" to "조식", "lun" to "중식", "eve" to "석식")
    }
}
