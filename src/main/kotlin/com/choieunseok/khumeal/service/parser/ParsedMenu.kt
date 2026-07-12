package com.choieunseok.khumeal.service.parser

import java.security.MessageDigest
import java.time.LocalDate

/**
 * 모든 소스(이미지 OCR / JSON / HTML)의 파싱 결과를 기존 저장 구조
 * (menu_info_meta → menu_info → menu_item)에 맞게 정규화한 형태.
 */
data class ParsedWeeklyMenu(
    val sourceVersion: String,     // IMAGE_GRPC: 게시글 ID / 웹 소스: 파싱 내용 해시 (변경 감지용)
    val prevSourceVersion: String,
    val baseDate: LocalDate,
    val imageUrl: String,          // 웹 소스는 ""
    val days: List<ParsedDailyMenu>
)

data class ParsedDailyMenu(
    val date: LocalDate,
    val corners: List<ParsedCorner>
)

data class ParsedCorner(
    val cornerName: String,
    val items: List<String>,
    val image: ByteArray? = null
)

/**
 * 웹 소스(게시글 ID 개념이 없는 소스)의 변경 감지용 콘텐츠 해시.
 * 같은 메뉴 내용이면 항상 같은 값 → menu_snapshot.source_version 자리에 저장해 기존 dedup 흐름 재사용.
 */
fun List<ParsedDailyMenu>.contentHash(): String {
    val canonical = this.sortedBy { it.date }.joinToString("\n") { day ->
        day.date.toString() + "|" + day.corners.joinToString(";") { corner ->
            corner.cornerName + ":" + corner.items.joinToString(",")
        }
    }
    return MessageDigest.getInstance("SHA-256")
        .digest(canonical.toByteArray())
        .joinToString("") { "%02x".format(it) }
}
