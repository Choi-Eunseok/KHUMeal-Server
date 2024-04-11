package com.choieunseok.khumeal.controller

import com.choieunseok.khumeal.service.StudentHallService
import org.slf4j.Logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stu-hall")
class StudentHallController(
    val service: StudentHallService,
    val logger: Logger
) {

    @GetMapping(path = ["ping", "ping/"])
    fun hello(): String {
        return "pong"
    }

    @GetMapping(path = ["", "/"])
    fun get(): String {
        val data = service.getContentsMap()
//        logger.info("\n"+data.toPrettyString("\n"))

        val boardId = service.getTodayMenuBoardId(data)
//        logger.info(boardId)

        val contentHtml = service.getContentHtml(boardId)
//        logger.info(contentHtml)

        return contentHtml.replace(
            "</body>",
            "<script type=\"text/javascript\">" +
                    "document.body.style.display = \"flex\";\n" +
                    "document.body.style.justifyContent = \"center\";\n" +
                    "document.body.style.alignItems = \"center\";\n" +
                    "document.getElementsByTagName(\"img\")[0].style.property = \"\";\n" +
                    "document.getElementsByTagName(\"img\")[0].style.width = window.innerWidth - 16;" +
                    "</script>" +
                    "</body>"
        )
    }
}