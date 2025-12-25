package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.MenuItemEntity
import com.choieunseok.khumeal.model.entity.MenuInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MenuItemRepository : JpaRepository<MenuItemEntity, UUID> {
    // 특정 코너(MenuInfo)에 속한 모든 음식 아이템 찾기 (인덱스 순서대로)
    fun findAllByMenuInfoOrderByIndexAsc(menuInfo: MenuInfoEntity): List<MenuItemEntity>
}