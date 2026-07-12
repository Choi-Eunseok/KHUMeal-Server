package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.CornerMenuEntity
import com.choieunseok.khumeal.model.entity.MenuItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MenuItemRepository : JpaRepository<MenuItemEntity, UUID> {
    // 특정 코너 메뉴에 속한 모든 음식 항목 찾기 (인덱스 순서대로)
    fun findAllByCornerMenuOrderByItemIndexAsc(cornerMenu: CornerMenuEntity): List<MenuItemEntity>
}
