package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*
import java.util.UUID

/**
 * 코너 메뉴 안의 개별 메뉴 항목(음식 하나).
 */
@Entity
@Table(name = "menu_item")
class MenuItemEntity(
    @Id
    @Column(name = "menu_item_uuid", columnDefinition = "UUID")
    val menuItemUuid: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corner_menu_uuid", nullable = false)
    val cornerMenu: CornerMenuEntity,

    @Column(name = "item_index", nullable = false)
    val itemIndex: Int,

    @Column(name = "item_name", nullable = false)
    val itemName: String
) : BaseTimeEntity()
