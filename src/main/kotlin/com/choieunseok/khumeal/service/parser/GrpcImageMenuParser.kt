package com.choieunseok.khumeal.service.parser

import com.choieunseok.khumeal.model.entity.MenuSnapshotEntity
import com.choieunseok.khumeal.model.entity.RestaurantEntity
import com.choieunseok.khumeal.model.entity.MenuSourceType
import com.choieunseok.khumeal.service.MenuGrpcClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * 기존 방식: 게시판에서 최신 메뉴 이미지 게시글을 찾아 gRPC menu-server로 OCR 파싱.
 */
@Component
class GrpcImageMenuParser(
    private val grpcClient: MenuGrpcClient
) : MenuSourceParser {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceType = MenuSourceType.IMAGE_GRPC

    override fun fetchRecent(restaurant: RestaurantEntity, lastSnapshot: MenuSnapshotEntity?): ParsedWeeklyMenu? {
        val startBoardId = lastSnapshot?.sourceVersion ?: ""

        var recentRes = grpcClient.findRecentMenu(
            baseUrl = restaurant.baseUrl,
            boardId = startBoardId,
            keyword = restaurant.keyword ?: ""
        )

        if (recentRes.boardId.isEmpty() && lastSnapshot != null) {
            log.info("${restaurant.name}: 기존 게시글 ID로 찾지 못해 이전 게시글 ID로 재시도합니다.")
            recentRes = grpcClient.findRecentMenu(
                baseUrl = restaurant.baseUrl,
                boardId = lastSnapshot.prevSourceVersion,
                keyword = restaurant.keyword ?: ""
            )
        }

        if (recentRes.boardId.isEmpty() || recentRes.boardId == startBoardId) {
            return null // 새 게시글 없음 → 최신 상태
        }

        val parseRes = grpcClient.parseMenu(recentRes.imageUrl)

        val days = parseRes.menuList
            .groupBy { it.dayInfo }
            .map { (dayInfo, protos) ->
                ParsedDailyMenu(
                    date = LocalDate.parse(dayInfo),
                    corners = protos.map { proto ->
                        ParsedCorner(
                            cornerName = proto.cornerInfo,
                            items = proto.menuItemsList,
                            image = proto.image.toByteArray()
                        )
                    }
                )
            }

        return ParsedWeeklyMenu(
            sourceVersion = recentRes.boardId,
            prevSourceVersion = recentRes.prevBoardId,
            baseDate = LocalDate.parse(recentRes.baseDate),
            imageUrl = recentRes.imageUrl,
            days = days
        )
    }
}
