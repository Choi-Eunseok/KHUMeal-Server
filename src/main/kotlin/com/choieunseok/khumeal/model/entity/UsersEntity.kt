package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UsersEntity(
    @Id
    @Column(name = "user_id")
    val userId: String
) : BaseTimeEntity()