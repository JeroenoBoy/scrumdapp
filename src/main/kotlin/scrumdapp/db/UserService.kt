package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.models.UserTable

interface UserService {
    suspend fun addUser(user: UserTable)
    suspend fun alterUser(id: Int, user: UserTable)
    suspend fun deleteUser(id: Int)
}