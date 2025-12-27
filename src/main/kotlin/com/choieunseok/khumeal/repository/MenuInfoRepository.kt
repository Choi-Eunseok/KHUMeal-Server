package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.MenuInfoEntity
import com.choieunseok.khumeal.model.entity.MenuInfoMetaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface MenuInfoRepository : JpaRepository<MenuInfoEntity, UUID> {

    @Query("""
        SELECT DISTINCT mi FROM MenuInfoEntity mi 
        LEFT JOIN FETCH mi.menuItems
        JOIN FETCH mi.menuInfoMeta mim
        JOIN FETCH mim.menuInfoName min
        WHERE min.menuInfoNameId = :nameId 
        AND mi.date BETWEEN :startDate AND :endDate
        ORDER BY mi.createdAt
    """)
    fun findByNameIdAndDateRangeOrderByCreatedAt(nameId: Int, startDate: LocalDate, endDate: LocalDate): List<MenuInfoEntity>

    @Query("""
    SELECT DISTINCT mi FROM MenuInfoEntity mi 
    LEFT JOIN FETCH mi.menuItems 
    JOIN FETCH mi.menuInfoMeta mim
    JOIN FETCH mim.menuInfoName min
    WHERE min.menuInfoNameId = :nameId 
    AND mi.date = :date
    ORDER BY mi.createdAt
    """)
    fun findByNameIdAndDateOrderByCreatedAt(nameId: Int, date: LocalDate): List<MenuInfoEntity>

}