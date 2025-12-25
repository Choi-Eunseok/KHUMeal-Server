package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.MenuInfoNameEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MenuInfoNameRepository : JpaRepository<MenuInfoNameEntity, Int> {
    // 식당 이름으로 찾기 (필요 시)
    fun findByName(name: String): MenuInfoNameEntity?
}