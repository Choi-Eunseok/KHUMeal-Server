package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcType
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType
import java.time.LocalDate
import java.util.UUID

/**
 * 특정 날짜·코너(조식/중식 등)의 메뉴. 코너 이미지가 있을 수 있고, 개별 메뉴 항목을 여러 개 가진다.
 */
@Entity
@Table(name = "corner_menu")
class CornerMenuEntity(
    @Id
    @Column(name = "corner_menu_uuid", columnDefinition = "UUID")
    val cornerMenuUuid: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snapshot_uuid", nullable = false)
    val snapshot: MenuSnapshotEntity,

    @Column(name = "menu_date", nullable = false)
    val menuDate: LocalDate,

    @Column(name = "corner_name", nullable = false)
    val cornerName: String,

    @Lob
    @JdbcType(BinaryJdbcType::class)
    @Column(name = "image")
    val image: ByteArray? = null,

    @OneToMany(mappedBy = "cornerMenu", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val menuItems: List<MenuItemEntity> = mutableListOf()
) : BaseTimeEntity()
