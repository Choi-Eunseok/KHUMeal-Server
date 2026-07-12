package com.choieunseok.khumeal.service.parser

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

class DormJsonMenuParserTest {

    private val parser = DormJsonMenuParser(jacksonObjectMapper(), RestTemplate())

    private val json = javaClass.getResource("/parser/dorm_weekly.json")!!.readText()

    @Test
    fun `주간 7일치 날짜를 모두 파싱한다`() {
        val days = parser.parseWeekly(json)

        assertEquals(7, days.size)
        assertEquals(LocalDate.of(2026, 6, 29), days.first().date)
        assertEquals(LocalDate.of(2026, 7, 5), days.last().date)
    }

    @Test
    fun `운영일은 조식-중식-석식 코너와 메뉴 항목으로 나뉜다`() {
        val monday = parser.parseWeekly(json).first { it.date == LocalDate.of(2026, 6, 29) }

        assertEquals(listOf("조식", "중식", "석식"), monday.corners.map { it.cornerName })

        val lunch = monday.corners.first { it.cornerName == "중식" }
        assertEquals(
            listOf("뚝)닭고기무국&당면사리", "쌀밥", "연두부&양념간장", "호박볶음", "고추지절임", "깍두기"),
            lunch.items
        )
    }

    @Test
    fun `미운영 요일은 코너가 비어 있다`() {
        val days = parser.parseWeekly(json)

        val saturday = days.first { it.date == LocalDate.of(2026, 7, 4) }
        val sunday = days.first { it.date == LocalDate.of(2026, 7, 5) }
        assertTrue(saturday.corners.isEmpty())
        assertTrue(sunday.corners.isEmpty())
    }

    @Test
    fun `같은 내용이면 콘텐츠 해시가 동일하다`() {
        val first = parser.parseWeekly(json).contentHash()
        val second = parser.parseWeekly(json).contentHash()

        assertEquals(first, second)
        assertEquals(64, first.length) // sha-256 hex → board_id(100자) 컬럼에 저장 가능
    }
}
