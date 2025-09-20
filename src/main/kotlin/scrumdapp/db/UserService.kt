package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.models.UserTable

data class User(
    val id: Int,
    val name: String,
    val discordId: Long,
    val profileImage: String,
)

interface UserService {
    suspend fun addUser(user: UserTable)
    suspend fun alterUser(id: Int, user: UserTable)
    suspend fun deleteUser(id: Int)
}