package com.jeroenvdg.scrumdapp.db

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val discordId: Long,
    val profileImage: String,
)

interface UserRepository {
    suspend fun getUser(id: Int): User?
    suspend fun getUserFromDiscordId(discordId: String): User?
    suspend fun getUsers(): List<User>
    suspend fun addUser(user: User): User?
    suspend fun alterUser(user: User): Boolean
    suspend fun deleteUser(user: User): Boolean
}