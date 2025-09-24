package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.models.UserTable
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val discordId: Long,
    val profileImage: String,
)

interface UserService {
    suspend fun getUser(id: Int): User?
    suspend fun getUsers(): List<User>
    suspend fun addUser(user: User): User?
    suspend fun alterUser(user: User): Boolean
    suspend fun deleteUser(user: User): Boolean
}