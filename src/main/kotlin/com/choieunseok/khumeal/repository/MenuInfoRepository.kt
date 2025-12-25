package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.MenuInfoEntity
import com.choieunseok.khumeal.model.entity.MenuInfoMetaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface MenuInfoRepository : JpaRepository<MenuInfoEntity, UUID> {
    // 특정 날짜의 모든 코너 메뉴 가져오기
    fun findAllByDate(date: LocalDate): List<MenuInfoEntity>

    // 특정 메타 데이터에 속한 메뉴들 찾기
    fun findAllByMenuInfoMeta(meta: MenuInfoMetaEntity): List<MenuInfoEntity>
}