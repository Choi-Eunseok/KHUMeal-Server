package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcType
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType
import java.util.UUID

@Entity
@Table(name = "menu_item")
class MenuItemEntity(
    @Id
    @Column(name = "menu_item_uuid", columnDefinition = "UUID")
    val menuItemUuid: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_info_uuid", nullable = false)
    val menuInfo: MenuInfoEntity,

    @Column(name = "index", nullable = false)
    val index: Int,

    @Column(name = "menu_item", nullable = false)
    val menuItem: String
) : BaseTimeEntity()