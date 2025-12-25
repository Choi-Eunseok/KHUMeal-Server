package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.MenuInfoMetaEntity
import com.choieunseok.khumeal.model.entity.MenuInfoNameEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MenuInfoMetaRepository : JpaRepository<MenuInfoMetaEntity, UUID> {
    // 특정 게시글 ID로 존재 여부 확인 (중복 저장 방지용)
    fun findByBoardId(boardId: String): MenuInfoMetaEntity?

    fun findFirstByMenuInfoNameOrderByBaseDateDesc(menuInfoName: MenuInfoNameEntity): MenuInfoMetaEntity?
}