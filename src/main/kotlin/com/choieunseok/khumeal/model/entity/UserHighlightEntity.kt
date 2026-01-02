package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*
import java.io.Serializable
import java.util.UUID

@Embeddable
data class UserHighlightId(
    @Column(name = "user_id")
    val userId: String = "",

    @Column(name = "menu_item_uuid")
    val menuItemUuid: UUID = UUID.randomUUID(),

    @Column(name = "menu_index")
    val menuIndex: Int = 0
) : Serializable

@Entity
@Table(name = "user_highlight")
class UserHighlightEntity(
    @EmbeddedId
    val id: UserHighlightId,

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UsersEntity,

    @MapsId("menuItemUuid")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_uuid")
    val menuItem: MenuItemEntity
) : BaseTimeEntity()