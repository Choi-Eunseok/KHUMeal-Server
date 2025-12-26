package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "menu_info_name")
class MenuInfoNameEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_info_name_id")
    val menuInfoNameId: Int? = null,

    @Column(name = "name")
    val name: String? = null,

    @Column(name = "keyword")
    val keyword: String? = null,

    @Column(name = "base_url", nullable = false)
    val baseUrl: String
) : BaseTimeEntity()