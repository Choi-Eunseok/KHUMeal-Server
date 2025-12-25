package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalTime

@Embeddable
data class UserSubscriptionId(
    val userId: String = "",
    val menuInfoNameId: Int = 0
) : Serializable

@Entity
@Table(name = "user_subscription")
class UserSubscriptionEntity(
    @EmbeddedId
    val id: UserSubscriptionId,

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UsersEntity,

    @MapsId("menuInfoNameId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_info_name_id")
    val menuInfoName: MenuInfoNameEntity,

    @Column(name = "time")
    val time: LocalTime? = null
)