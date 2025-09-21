package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.models.UserTable


data class User(
    val id: Int,
    val name: String,
    val discordId: Long,
    val profileImage: String,
)

interface UserService {
    suspend fun getUser(id: Int): User?
    suspend fun getUsers(): List<User>
    suspend fun addUser(user: UserTable.Users): User?
    suspend fun alterUser(user: UserTable.Users): Boolean
    suspend fun deleteUser(user: UserTable.Users): Boolean
}