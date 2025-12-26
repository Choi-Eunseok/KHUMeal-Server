package com.choieunseok.khumeal.service

import com.choieunseok.khumeal.model.dto.RestaurantResponse
import com.choieunseok.khumeal.repository.MenuInfoNameRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RestaurantService(
    private val nameRepo: MenuInfoNameRepository,
) {

    @Transactional(readOnly = true)
    fun getAllRestaurants(): List<RestaurantResponse> {
        return nameRepo.findAll().map {
            RestaurantResponse(id = it.menuInfoNameId!!, name = it.name!!)
        }
    }

}