package com.choieunseok.khumeal.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class UsersEntity(
    @Id
    @Column(name = "user_id")
    val userId: String,

    @Column(name = "fcm_token")
    var fcmToken: String?,

    var platform: String,

    @Column(name = "last_login")
    var lastLogin: LocalDateTime = LocalDateTime.now()
) : BaseTimeEntity()