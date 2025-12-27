package com.choieunseok.khumeal.service

import com.choieunseok.khumeal.getWeekRange
import com.choieunseok.khumeal.hasValidCorner
import com.choieunseok.khumeal.model.dto.MenuItem
import com.choieunseok.khumeal.model.dto.MenuResponse
import com.choieunseok.khumeal.repository.MenuInfoRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MenuService(
    private val infoRepo: MenuInfoRepository
) {

    @Transactional(readOnly = true)
    fun getDailyMenuByRestaurant(nameId: Int, date: LocalDate): List<MenuItem> {
        val infos = infoRepo.findByNameIdAndDateOrderByCreatedAt(nameId, date)

        return infos.filter { it.hasValidCorner() }
            .map { info ->
            MenuItem(
                menuInfoUuid = info.menuInfoUuid.toString(),
                cornerName = info.cornerInfo,
                items = info.menuItems.sortedBy { it.index }.map { it.menuItem },
                imageUrl = if (info.image != null) "/api/image/${info.menuInfoUuid}" else null
            )
        }
    }

    @Transactional(readOnly = true)
    fun getThisWeekMenusByRestaurant(nameId: Int): List<MenuResponse> {
        val range = getWeekRange(LocalDate.now())
        return getMenusInRange(nameId, range.first, range.second)
    }

    @Transactional(readOnly = true)
    fun getNextWeekMenusByRestaurant(nameId: Int): List<MenuResponse> {
        val nextSaturday = getWeekRange(LocalDate.now()).first.plusWeeks(1)
        val range = getWeekRange(nextSaturday)
        return getMenusInRange(nameId, range.first, range.second)
    }

    private fun getMenusInRange(nameId: Int, startDate: LocalDate, endDate: LocalDate): List<MenuResponse> {
        val allInfos = infoRepo.findByNameIdAndDateRangeOrderByCreatedAt(nameId, startDate, endDate)

        return allInfos.groupBy { it.date }
            .map { (date, infos) ->
                MenuResponse(
                    restaurantName = infos.first().menuInfoMeta.menuInfoName.name ?: "",
                    date = date.toString(),
                    menuItems = infos.filter { it.hasValidCorner() }
                        .map { info ->
                            val itemList = info.menuItems.sortedBy { it.index }.map { it.menuItem }

                            MenuItem(
                                menuInfoUuid = info.menuInfoUuid.toString(),
                                cornerName = info.cornerInfo,
                                items = itemList.ifEmpty { listOf("") },
                                imageUrl = if (info.image != null) "/api/image/${info.menuInfoUuid}" else null
                            )
                        }
                )
            }.sortedBy { it.date }
    }

}