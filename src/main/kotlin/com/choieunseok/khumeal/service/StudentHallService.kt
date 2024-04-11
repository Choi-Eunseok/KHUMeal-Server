package com.choieunseok.khumeal.service

import com.choieunseok.khumeal.*
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@Service
class StudentHallService : RestTemplateClient() {
    val viewUrl = "https://www.khu.ac.kr/kor/user/bbs/BMSR00040/view.do"

    val listUri = UriComponentsBuilder
        .fromUriString("https://www.khu.ac.kr/kor/user/bbs/BMSR00040/list.do")
        .queryParam("menuNo", "200283")
        .queryParam("catId", "137")
//        .queryParam("pageIndex", "10")
        .build()
        .toUri()

    fun getContentsMap(): Map<String, Date?> {
        val resultMap = sortedMapOf<String, Date?>(Comparator.reverseOrder())

        val listHtmlString: String = get(listUri) ?: return resultMap
        val data = Jsoup.parse(listHtmlString)

        val contentsList = data.select(".board01").select("tbody").select("tr")
        contentsList.forEach {
            val aElement = it.select("a")

            val boardId = "'\\d+'".toRegex().find(aElement.attr("href"))?.value?.replace("'", "") ?: ""
            val startDateString = "\\d+".toRegex().find(aElement.text())?.value ?: ""
            val writeDateString = it.select("td")[3].text()

            if (boardId.isEmpty() || startDateString.isEmpty())
                return@forEach

            var startDate: Date? = null
            when (startDateString.length) {
                8 -> startDate = startDateString.toDateWithFormat("yyyyMMdd")
                6 -> startDate = startDateString.toDateWithFormat("yyMMdd")
                4 -> startDate = (writeDateString.substring(0 until 4) + startDateString).toDateWithFormat("yyyyMMdd")
            }

            resultMap.put(boardId, startDate)
        }

        return resultMap
    }

    fun getTodayMenuBoardId(list: Map<String, Date?>): String {
        var result = ""

        run breaker@{
            list.forEach {
                if (it.value?.isThisWeek() ?: return@forEach) {
                    result = it.key
                    return@breaker
                }
            }
        }
        return result.ifEmpty { list.keys.first() }
    }

    fun getContentHtml(boardId: String): String {
        val body = LinkedMultiValueMap<String, String>()
        body.add("menuNo", "200283")
        body.add("catId", "137")
        body.add("boardId", boardId)

        val viewHtmlString: String = post(viewUrl, body) ?: return ""
        val data = Jsoup.parse(viewHtmlString)

        data.select("img").attr("style", "width: 100%;")

        return Jsoup.parse(data.select(".row.contents").toString()).toString()
    }
}