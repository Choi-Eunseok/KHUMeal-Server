package com.choieunseok.khumeal.service.parser

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

class HufsHtmlMenuParserTest {

    private val parser = HufsHtmlMenuParser(jacksonObjectMapper(), RestTemplate())

    private val html = javaClass.getResource("/parser/hufs_menu.html")!!.readText()

    @Test
    fun `thead의 date span에서 일~토 7일을 파싱한다`() {
        val days = parser.parseTable(html)

        assertEquals(7, days.size)
        assertEquals(LocalDate.of(2026, 7, 5), days.first().date)
        assertEquals(LocalDate.of(2026, 7, 11), days.last().date)
    }

    @Test
    fun `운영일은 코너별 메뉴로 나뉜다`() {
        val monday = parser.parseTable(html).first { it.date == LocalDate.of(2026, 7, 6) }

        assertEquals(listOf("조식", "중식(1)", "중식(2)", "중식(면)", "석식"), monday.corners.map { it.cornerName })

        val lunch1 = monday.corners.first { it.cornerName == "중식(1)" }
        // 메뉴 항목 뒤에 칼로리·가격이 붙는다
        assertEquals(
            listOf("피자돈가스", "크림스프", "곁들임야채", "샐러드", "김치", "돈육:국내산", "774Kcal", "4,000원"),
            lunch1.items
        )
    }

    @Test
    fun `전 셀이 no-menu인 날(주말)은 코너가 비어 있다`() {
        val days = parser.parseTable(html)

        val sunday = days.first { it.date == LocalDate.of(2026, 7, 5) }
        val saturday = days.first { it.date == LocalDate.of(2026, 7, 11) }
        assertTrue(sunday.corners.isEmpty())
        assertTrue(saturday.corners.isEmpty())
    }

    @Test
    fun `칼로리와 가격이 항목 끝에 붙는다`() {
        val tuesdayDinner = parser.parseTable(html)
            .first { it.date == LocalDate.of(2026, 7, 7) }
            .corners.first { it.cornerName == "석식" }

        assertEquals(
            listOf("갈비탕", "도라지오이생채", "과일샐러드", "깍두기", "우갈비:미국,우:호주", "659Kcal", "4,000원"),
            tuesdayDinner.items
        )
        // 칼로리가 비어 있는 셀은 가격만 붙는다
        val meonMenu = parser.parseTable(html)
            .first { it.date == LocalDate.of(2026, 7, 7) }
            .corners.first { it.cornerName == "중식(면)" }
        assertEquals(listOf("냉면", "520Kcal", "3,000원"), meonMenu.items)
    }

    @Test
    fun `같은 내용이면 콘텐츠 해시가 동일하다`() {
        assertEquals(
            parser.parseTable(html).contentHash(),
            parser.parseTable(html).contentHash()
        )
    }
}
