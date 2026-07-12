package com.choieunseok.khumeal.service

import com.choieunseok.khumeal.model.entity.*
import com.choieunseok.khumeal.repository.*
import com.choieunseok.khumeal.service.parser.MenuSourceParser
import com.choieunseok.khumeal.service.parser.ParsedWeeklyMenu
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory

@Service
class MenuSyncService(
    parsers: List<MenuSourceParser>,
    private val restaurantRepo: RestaurantRepository,
    private val snapshotRepo: MenuSnapshotRepository,
    private val cornerMenuRepo: CornerMenuRepository,
    private val itemRepo: MenuItemRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val parserMap = parsers.associateBy { it.sourceType }

    @Transactional
    fun syncAllMenus() {
        val allRestaurants = restaurantRepo.findAll()
        log.info("총 ${allRestaurants.size}개의 식당 메뉴 동기화를 시작합니다.")

        allRestaurants.forEach { restaurant ->
            try {
                syncSingleRestaurant(restaurant)
                log.info("${restaurant.name} 동기화 완료")
            } catch (e: Exception) {
                log.error("${restaurant.name} 동기화 중 오류 발생: ${e.message}", e)
            }
        }
    }

    @Transactional
    fun syncLatestMenu(restaurantId: Int) {
        val restaurant = restaurantRepo.findById(restaurantId)
            .orElseThrow { IllegalArgumentException("식당 정보를 찾을 수 없습니다. ID: $restaurantId") }
        syncSingleRestaurant(restaurant)
    }

    private fun syncSingleRestaurant(restaurant: RestaurantEntity) {
        when (val primary = fetchFrom(restaurant.primarySource, restaurant)) {
            is FetchOutcome.NewMenu -> {
                saveMenu(restaurant, restaurant.primarySource, primary.menu)
                return
            }
            FetchOutcome.UpToDate -> {
                log.info("${restaurant.name}: 이미 최신 상태입니다. (source: ${restaurant.primarySource})")
                return
            }
            FetchOutcome.NothingParsed -> Unit // 아래에서 fallback 시도
        }

        val fallbackSource = restaurant.fallbackSource
        if (fallbackSource == null) {
            log.info("${restaurant.name}: 파싱 결과가 없어 기존 DB 데이터를 유지합니다.")
            return
        }

        log.info("${restaurant.name}: ${restaurant.primarySource} 파싱 실패/빈 결과 → $fallbackSource 으로 재시도합니다.")
        when (val fallback = fetchFrom(fallbackSource, restaurant)) {
            is FetchOutcome.NewMenu -> saveMenu(restaurant, fallbackSource, fallback.menu)
            else -> log.info("${restaurant.name}: fallback에서도 새 메뉴가 없어 기존 DB 데이터를 유지합니다.")
        }
    }

    private fun fetchFrom(sourceType: MenuSourceType, restaurant: RestaurantEntity): FetchOutcome {
        val parser = parserMap[sourceType]
            ?: throw IllegalStateException("등록된 파서가 없습니다: $sourceType")
        val lastSnapshot = snapshotRepo.findFirstByRestaurantAndOriginSourceOrderByCreatedAtDesc(restaurant, sourceType)

        return try {
            val menu = parser.fetchRecent(restaurant, lastSnapshot)
            when {
                menu == null -> FetchOutcome.UpToDate
                // 파싱 결과가 비어 있으면 절대 저장하지 않는다 → 기존 데이터 보호
                menu.days.none { it.corners.isNotEmpty() } -> FetchOutcome.NothingParsed
                else -> FetchOutcome.NewMenu(menu)
            }
        } catch (e: Exception) {
            log.error("${restaurant.name}: $sourceType 파싱 중 오류 발생: ${e.message}", e)
            FetchOutcome.NothingParsed
        }
    }

    private fun saveMenu(restaurant: RestaurantEntity, sourceType: MenuSourceType, menu: ParsedWeeklyMenu) {
        val snapshotEntity = MenuSnapshotEntity(
            restaurant = restaurant,
            baseDate = menu.baseDate,
            sourceVersion = menu.sourceVersion,
            imageUrl = menu.imageUrl,
            prevSourceVersion = menu.prevSourceVersion,
            originSource = sourceType
        )
        val savedSnapshot = snapshotRepo.save(snapshotEntity)

        menu.days.forEach { day ->
            day.corners.forEach { corner ->
                val cornerMenuEntity = CornerMenuEntity(
                    snapshot = savedSnapshot,
                    menuDate = day.date,
                    cornerName = corner.cornerName,
                    image = corner.image
                )
                val savedCornerMenu = cornerMenuRepo.save(cornerMenuEntity)

                corner.items.forEachIndexed { index, itemText ->
                    val itemEntity = MenuItemEntity(
                        cornerMenu = savedCornerMenu,
                        itemIndex = index,
                        itemName = itemText
                    )
                    itemRepo.save(itemEntity)
                }
            }
        }
        log.info("${restaurant.name}: $sourceType 소스에서 ${menu.days.size}일치 메뉴를 저장했습니다.")
    }

    private sealed interface FetchOutcome {
        data class NewMenu(val menu: ParsedWeeklyMenu) : FetchOutcome
        data object UpToDate : FetchOutcome       // 변경 없음 → 아무것도 안 함
        data object NothingParsed : FetchOutcome  // 실패 또는 빈 결과 → fallback 시도
    }
}
