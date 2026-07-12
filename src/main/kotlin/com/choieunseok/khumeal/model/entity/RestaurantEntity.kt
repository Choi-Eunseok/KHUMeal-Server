package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "restaurant")
class RestaurantEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    val restaurantId: Int? = null,

    @Column(name = "name")
    val name: String? = null,

    @Column(name = "keyword")
    val keyword: String? = null,

    @Column(name = "base_url", nullable = false)
    val baseUrl: String,

    // 메뉴를 가져올 기본 소스 (기존 식당은 IMAGE_GRPC)
    @Enumerated(EnumType.STRING)
    @Column(name = "primary_source", length = 30, nullable = false)
    val primarySource: MenuSourceType = MenuSourceType.IMAGE_GRPC,

    // 기본 소스가 실패하거나 빈 결과일 때 시도할 소스 (예: DORM_JSON 실패 시 IMAGE_GRPC)
    @Enumerated(EnumType.STRING)
    @Column(name = "fallback_source", length = 30)
    val fallbackSource: MenuSourceType? = null,

    // 웹 파싱 소스의 엔드포인트. null이면 baseUrl 사용
    @Column(name = "source_url", length = 500)
    val sourceUrl: String? = null,

    // 파서별 요청 파라미터 JSON. 예) DORM_JSON: {"locgbn":"K1","foGbn":"stu"} / HUFS_HTML: {"selCafId":"h101"}
    @Column(name = "source_config", length = 500)
    val sourceConfig: String? = null
) : BaseTimeEntity()
