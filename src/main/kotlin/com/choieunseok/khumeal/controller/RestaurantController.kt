package com.choieunseok.khumeal.controller

import com.choieunseok.khumeal.model.dto.RestaurantResponse
import com.choieunseok.khumeal.service.RestaurantService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/restaurant")
class RestaurantController(
    val restaurantService: RestaurantService
) {

    @GetMapping("", "/")
    fun getRestaurants(): ResponseEntity<List<RestaurantResponse>> {
        val restaurants = restaurantService.getAllRestaurants()
        return ResponseEntity.ok(restaurants)
    }

}