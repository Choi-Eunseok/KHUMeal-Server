package com.choieunseok.khumeal.repository

import com.choieunseok.khumeal.model.entity.UsersEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UsersEntity, String> // ID가 String(user_id)