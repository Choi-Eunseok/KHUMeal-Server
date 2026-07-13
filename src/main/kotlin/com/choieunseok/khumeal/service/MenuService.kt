package com.choieunseok.khumeal.service

import com.choieunseok.khumeal.getWeekRange
import com.choieunseok.khumeal.hasValidCorner
import com.choieunseok.khumeal.model.dto.MenuInfo
import com.choieunseok.khumeal.model.dto.MenuItem
import com.choieunseok.khumeal.model.dto.MenuResponse
import com.choieunseok.khumeal.repository.CornerMenuRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MenuService(
    private val cornerMenuRepo: CornerMenuRepository
) {

    @Transactional(readOnly = true)
    fun getDailyMenuByRestaurant(restaurantId: Int, date: LocalDate): List<MenuInfo> {
        val corners = cornerMenuRepo.findAllByLatestSnapshotAndDate(restaurantId, date)

        return corners.filter { it.hasValidCorner() }
            .map { corner ->
            MenuInfo(
                menuInfoUuid = corner.cornerMenuUuid.toString(),
                cornerName = corner.cornerName,
                items = corner.menuItems.sortedBy { it.itemIndex }.map { itemEntity ->
                    MenuItem(
                        menuItemUuid = itemEntity.menuItemUuid.toString(),
                        menuItemName = itemEntity.itemName
                    )
                },
                imageUrl = "/api/image/${corner.cornerMenuUuid}"
            )
        }
    }

    @Transactional(readOnly = true)
    fun getThisWeekMenusByRestaurant(restaurantId: Int): List<MenuResponse> {
        val range = getWeekRange(LocalDate.now())
        return getMenusInRange(restaurantId, range.first, range.second)
    }

    @Transactional(readOnly = true)
    fun getNextWeekMenusByRestaurant(restaurantId: Int): List<MenuResponse> {
        val nextMonday = getWeekRange(LocalDate.now()).second.plusDays(1)
        val range = getWeekRange(nextMonday)
        return getMenusInRange(restaurantId, range.first, range.second)
    }

    private fun getMenusInRange(restaurantId: Int, startDate: LocalDate, endDate: LocalDate): List<MenuResponse> {
        val allCorners = cornerMenuRepo.findAllByLatestSnapshotAndDateRange(restaurantId, startDate, endDate)

        return allCorners.groupBy { it.menuDate }
            .map { (date, corners) ->
                MenuResponse(
                    restaurantName = corners.first().snapshot.restaurant.name ?: "",
                    date = date.toString(),
                    menuInfos = corners.filter { it.hasValidCorner() }
                        .map { corner ->
                            MenuInfo(
                                menuInfoUuid = corner.cornerMenuUuid.toString(),
                                cornerName = corner.cornerName,
                                items = corner.menuItems.sortedBy { it.itemIndex }.map { itemEntity ->
                                    MenuItem(
                                        menuItemUuid = itemEntity.menuItemUuid.toString(),
                                        menuItemName = itemEntity.itemName
                                    )
                                },
                                imageUrl = "/api/image/${corner.cornerMenuUuid}"
                            )
                        }
                )
            }.sortedBy { it.date }
    }
}
