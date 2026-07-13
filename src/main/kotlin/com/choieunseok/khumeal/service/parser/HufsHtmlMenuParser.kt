package com.choieunseok.khumeal.service.parser

import com.choieunseok.khumeal.model.entity.MenuSnapshotEntity
import com.choieunseok.khumeal.model.entity.RestaurantEntity
import com.choieunseok.khumeal.model.entity.MenuSourceType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

/**
 * 한국외대 학식 주간 메뉴 HTML 파서.
 * POST https://www.hufs.ac.kr/cafeteria/hufs/1/getMenu.do
 *      (selCafId, selWeekFirstDay, selWeekLastDay, selYear, selMonth)
 *
 * 응답: <table> — thead th의 <span class="date" id="date_YYYY-MM-DD">가 날짜 컬럼,
 * tbody 각 행이 코너(조식/중식(1)/중식(2)/중식(면)/석식), td.menu > ul > li 가 메뉴 항목.
 * td.no-menu("등록된 메뉴가 없습니다")와 방학 공지·원산지 표기는 걸러낸다.
 * HUFS의 한 주는 일요일~토요일.
 */
@Component
class HufsHtmlMenuParser(
    private val objectMapper: ObjectMapper,
    private val restTemplate: RestTemplate
) : MenuSourceParser {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceType = MenuSourceType.HUFS_HTML

    data class Config(
        val selCafId: String = "h101"
    )

    override fun fetchRecent(restaurant: RestaurantEntity, lastSnapshot: MenuSnapshotEntity?): ParsedWeeklyMenu? {
        val url = restaurant.sourceUrl ?: restaurant.baseUrl
        val config = restaurant.sourceConfig
            ?.let { objectMapper.readValue<Config>(it) }
            ?: Config()

        val today = LocalDate.now()
        // 이번 주 + 다음 주를 함께 가져온다
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

    private fun fetchWeek(url: String, config: Config, baseDate: LocalDate): List<ParsedDailyMenu> {
        // baseDate가 속한 일요일~토요일 주. 연/월은 일요일 기준
        val sunday = baseDate.minusDays((baseDate.dayOfWeek.value % 7).toLong())
        val saturday = sunday.plusDays(6)

        val form = LinkedMultiValueMap<String, String>().apply {
            add("selCafId", config.selCafId)
            add("selWeekFirstDay", sunday.dayOfMonth.toString())
            add("selWeekLastDay", saturday.dayOfMonth.toString())
            add("selYear", sunday.year.toString())
            add("selMonth", "%02d".format(sunday.monthValue))
        }
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED }
        val body = restTemplate.postForObject(url, HttpEntity(form, headers), String::class.java)
            ?: throw IllegalStateException("외대 메뉴 응답이 비어 있습니다: $url")
        return parseTable(body)
    }

    private fun fetchWeekOrEmpty(url: String, config: Config, baseDate: LocalDate): List<ParsedDailyMenu> =
        try {
            fetchWeek(url, config, baseDate)
        } catch (e: Exception) {
            log.warn("외대 다음 주 메뉴 조회 실패(무시하고 진행): ${e.message}")
            emptyList()
        }

    internal fun parseTable(html: String): List<ParsedDailyMenu> {
        val table = Jsoup.parse(html).selectFirst("table") ?: return emptyList()

        // 날짜 컬럼: <span class="date" id="date_2026-07-05">07/05</span>
        val dates = table.select("thead th span.date").mapNotNull { span ->
            runCatching { LocalDate.parse(span.id().removePrefix("date_")) }.getOrNull()
        }
        if (dates.isEmpty()) return emptyList()

        val cornersByDate = dates.associateWith { mutableListOf<ParsedCorner>() }

        table.select("tbody tr").forEach { row ->
            // <th>중식(1)<br>(11:00 ~ 14:30)</th> → "중식(1)" (첫 <br> 앞부분만)
            val cornerName = row.selectFirst("th")
                ?.let { Jsoup.parse(it.html().substringBefore("<br")).text().trim() }
                ?.takeIf { it.isNotBlank() }
                ?: return@forEach

            row.select("td").forEachIndexed { columnIndex, td ->
                val date = dates.getOrNull(columnIndex) ?: return@forEachIndexed
                if (td.hasClass("no-menu")) return@forEachIndexed

                val items = td.select("li")
                    .map { it.text().trim() }
                    .filter { it.isNotBlank() }
                    .toMutableList()

                // 칼로리(<p class="calorie">620Kcal)와 가격(<p class="pay">4,000원)도 항목에 덧붙인다
                td.selectFirst("p.calorie")?.text()?.trim()?.takeIf { it.isNotBlank() }?.let { items.add(it) }
                td.selectFirst("p.pay")?.text()?.trim()?.takeIf { it.isNotBlank() }?.let { items.add(it) }

                if (items.isNotEmpty()) {
                    cornersByDate.getValue(date).add(ParsedCorner(cornerName, items))
                }
            }
        }

        return cornersByDate.map { (date, corners) -> ParsedDailyMenu(date, corners) }
    }
}
