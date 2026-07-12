package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.RestaurantEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RestaurantRepository : JpaRepository<RestaurantEntity, Int> {
    // 식당 이름으로 찾기 (필요 시)
    fun findByName(name: String): RestaurantEntity?
}
