package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.CornerMenuEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface CornerMenuRepository : JpaRepository<CornerMenuEntity, UUID> {

    @Query("""
        SELECT DISTINCT cm FROM CornerMenuEntity cm
        LEFT JOIN FETCH cm.menuItems
        JOIN FETCH cm.snapshot s
        JOIN FETCH s.restaurant r
        WHERE r.restaurantId = :restaurantId
        AND cm.menuDate BETWEEN :startDate AND :endDate
        AND s.snapshotUuid = (
            SELECT sub.snapshotUuid
            FROM MenuSnapshotEntity sub
            WHERE sub.restaurant.restaurantId = :restaurantId
            ORDER BY sub.createdAt DESC
            LIMIT 1
        )
        ORDER BY cm.createdAt
    """)
    fun findAllByLatestSnapshotAndDateRange(restaurantId: Int, startDate: LocalDate, endDate: LocalDate): List<CornerMenuEntity>

    @Query("""
        SELECT DISTINCT cm FROM CornerMenuEntity cm
        LEFT JOIN FETCH cm.menuItems
        JOIN FETCH cm.snapshot s
        JOIN FETCH s.restaurant r
        WHERE r.restaurantId = :restaurantId
        AND cm.menuDate = :date
        AND s.snapshotUuid = (
            SELECT sub.snapshotUuid
            FROM MenuSnapshotEntity sub
            WHERE sub.restaurant.restaurantId = :restaurantId
            ORDER BY sub.createdAt DESC
            LIMIT 1
        )
        ORDER BY cm.createdAt
    """)
    fun findAllByLatestSnapshotAndDate(restaurantId: Int, date: LocalDate): List<CornerMenuEntity>

}
