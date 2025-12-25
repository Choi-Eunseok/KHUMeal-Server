package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcType
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "menu_info")
class MenuInfoEntity(
    @Id
    @Column(name = "menu_info_uuid", columnDefinition = "UUID")
    val menuInfoUuid: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_info_meta_uuid", nullable = false)
    val menuInfoMeta: MenuInfoMetaEntity,

    @Column(name = "date", nullable = false)
    val date: LocalDate,

    @Column(name = "corner_info", nullable = false)
    val cornerInfo: String,

    @Lob
    @JdbcType(BinaryJdbcType::class)
    @Column(name = "image")
    val image: ByteArray? = null
)