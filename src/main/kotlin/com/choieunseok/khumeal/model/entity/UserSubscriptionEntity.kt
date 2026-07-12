package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalTime

@Embeddable
data class UserSubscriptionId(
    val userId: String = "",
    val restaurantId: Int = 0
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

    @MapsId("restaurantId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    val restaurant: RestaurantEntity,

    @Column(name = "time")
    val time: LocalTime? = null
) : BaseTimeEntity()