package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.MenuSnapshotEntity
import com.choieunseok.khumeal.model.entity.MenuSourceType
import com.choieunseok.khumeal.model.entity.RestaurantEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MenuSnapshotRepository : JpaRepository<MenuSnapshotEntity, UUID> {

    fun findFirstByRestaurantOrderByCreatedAtDesc(restaurant: RestaurantEntity): MenuSnapshotEntity?

    fun findFirstByRestaurantAndOriginSourceOrderByCreatedAtDesc(
        restaurant: RestaurantEntity,
        originSource: MenuSourceType
    ): MenuSnapshotEntity?
}
