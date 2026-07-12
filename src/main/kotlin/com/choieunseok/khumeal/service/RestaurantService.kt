package com.choieunseok.khumeal.service

import com.choieunseok.khumeal.model.dto.RestaurantResponse
import com.choieunseok.khumeal.repository.RestaurantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RestaurantService(
    private val restaurantRepo: RestaurantRepository,
) {

    @Transactional(readOnly = true)
    fun getAllRestaurants(): List<RestaurantResponse> {
        return restaurantRepo.findAll().map {
            RestaurantResponse(id = it.restaurantId!!, name = it.name!!)
        }
    }

}