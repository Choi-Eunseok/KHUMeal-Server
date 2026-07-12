package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

/**
 * 한 소스에서 특정 시점에 가져온 주간 메뉴 버전. 읽기 경로는 식당별 최신 스냅샷만 조회한다.
 */
@Entity
@Table(name = "menu_snapshot")
class MenuSnapshotEntity(
    @Id
    @Column(name = "snapshot_uuid", columnDefinition = "UUID")
    val snapshotUuid: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    val restaurant: RestaurantEntity,

    @Column(name = "base_date", nullable = false)
    val baseDate: LocalDate,

    // 소스의 버전 키. IMAGE_GRPC는 게시글 ID, 웹 소스는 파싱 내용 해시 → 변경 감지에 사용
    @Column(name = "source_version", length = 100, nullable = false)
    val sourceVersion: String,

    @Column(name = "image_url", nullable = false)
    val imageUrl: String,

    @Column(name = "prev_source_version", length = 100, nullable = false)
    val prevSourceVersion: String,

    // 이 스냅샷이 어느 소스에서 왔는지. 소스별 최신 상태 비교에 사용
    @Enumerated(EnumType.STRING)
    @Column(name = "origin_source", length = 30, nullable = false)
    val originSource: MenuSourceType = MenuSourceType.IMAGE_GRPC
) : BaseTimeEntity()
