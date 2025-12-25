package com.choieunseok.khumeal.service

import com.choieunseok.khumeal.model.entity.*
import com.choieunseok.khumeal.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import org.slf4j.LoggerFactory

@Service
class MenuSyncService(
    private val grpcClient: MenuGrpcClient,
    private val nameRepo: MenuInfoNameRepository,
    private val metaRepo: MenuInfoMetaRepository,
    private val infoRepo: MenuInfoRepository,
    private val itemRepo: MenuItemRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun syncAllMenus() {
        val allRestaurants = nameRepo.findAll()
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
    fun syncLatestMenu(nameId: Int) {
        val infoName = nameRepo.findById(nameId)
            .orElseThrow { IllegalArgumentException("식당 정보를 찾을 수 없습니다. ID: $nameId") }
        syncSingleRestaurant(infoName)
    }

    private fun syncSingleRestaurant(infoName: MenuInfoNameEntity) {
        val recentMenuInfoMeta = metaRepo.findFirstByMenuInfoNameOrderByBaseDateDesc(infoName)

        val startBoardId = recentMenuInfoMeta?.boardId ?: ""

        var recentRes = grpcClient.findRecentMenu(
            baseUrl = infoName.baseUrl,
            boardId = startBoardId,
            keyword = infoName.keyword ?: ""
        )

        if (recentRes.boardId.isEmpty() && recentMenuInfoMeta != null) {
            log.info("${infoName.name}: 기존 boardId로 찾지 못해 prevBoardId로 재시도합니다.")
            recentRes = grpcClient.findRecentMenu(
                baseUrl = infoName.baseUrl,
                boardId = recentMenuInfoMeta.prevBoardId,
                keyword = infoName.keyword ?: ""
            )
        }

        if (recentRes.boardId.isEmpty() || recentRes.boardId == startBoardId) {
            log.info("${infoName.name}: 새로운 메뉴 업데이트가 없거나 이미 최신 상태입니다. (boardId: ${recentRes.boardId})")
            return
        }

        val metaEntity = MenuInfoMetaEntity(
            menuInfoName = infoName,
            baseDate = LocalDate.parse(recentRes.baseDate),
            boardId = recentRes.boardId,
            imageUrl = recentRes.imageUrl,
            prevBoardId = recentRes.prevBoardId
        )
        val savedMeta = metaRepo.save(metaEntity)

        val parseRes = grpcClient.parseMenu(savedMeta.imageUrl)

        parseRes.menuList.forEach { menuProto ->
            val infoEntity = MenuInfoEntity(
                menuInfoMeta = savedMeta,
                date = LocalDate.parse(menuProto.dayInfo),
                cornerInfo = menuProto.cornerInfo,
                image = menuProto.image.toByteArray()
            )
            val savedInfo = infoRepo.save(infoEntity)

            menuProto.menuItemsList.forEachIndexed { index, itemText ->
                val itemEntity = MenuItemEntity(
                    menuInfo = savedInfo,
                    index = index,
                    menuItem = itemText
                )
                itemRepo.save(itemEntity)
            }
        }
    }
}