package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.MenuInfoMetaEntity
import com.choieunseok.khumeal.model.entity.MenuInfoNameEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface MenuInfoMetaRepository : JpaRepository<MenuInfoMetaEntity, UUID> {

    fun findFirstByMenuInfoNameOrderByBaseDateDesc(menuInfoName: MenuInfoNameEntity): MenuInfoMetaEntity?

}