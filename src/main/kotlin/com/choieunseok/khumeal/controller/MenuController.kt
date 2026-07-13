package com.choieunseok.khumeal.controller

import com.choieunseok.khumeal.model.dto.MenuInfo
import com.choieunseok.khumeal.model.dto.MenuResponse
import com.choieunseok.khumeal.service.MenuService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/menu")
class MenuController(
    private val menuService: MenuService
) {

    private val cacheControl = CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic() // 1시간 동안 캐싱

    @GetMapping("/daily/{nameId}")
    fun getDailyMenu(
        @PathVariable nameId: Int,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?
    ): ResponseEntity<List<MenuInfo>> {
        val targetDate = date ?: LocalDate.now()
        val menus = menuService.getDailyMenuByRestaurant(nameId, targetDate)
        return ResponseEntity.ok(menus)
    }

    @GetMapping("/week/this/{nameId}")
    fun getThisWeekMenus(@PathVariable nameId: Int): ResponseEntity<List<MenuResponse>> {
        val menus = menuService.getThisWeekMenusByRestaurant(nameId)
        return ResponseEntity.ok().cacheControl(cacheControl).body(menus)
    }

    @GetMapping("/week/next/{nameId}")
    fun getNextWeekMenus(@PathVariable nameId: Int): ResponseEntity<List<MenuResponse>> {
        val menus = menuService.getNextWeekMenusByRestaurant(nameId)
        return ResponseEntity.ok().cacheControl(cacheControl).body(menus)
    }

}