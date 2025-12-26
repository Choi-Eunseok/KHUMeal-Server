package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "menu_info_meta")
class MenuInfoMetaEntity(
    @Id
    @Column(name = "menu_info_meta_uuid", columnDefinition = "UUID")
    val menuInfoMetaUuid: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_info_name_id", nullable = false)
    val menuInfoName: MenuInfoNameEntity,

    @Column(name = "base_date", nullable = false)
    val baseDate: LocalDate,

    @Column(name = "board_id", length = 100, nullable = false)
    val boardId: String,

    @Column(name = "image_url", nullable = false)
    val imageUrl: String,

    @Column(name = "prev_board_id", length = 100, nullable = false)
    val prevBoardId: String
) : BaseTimeEntity()