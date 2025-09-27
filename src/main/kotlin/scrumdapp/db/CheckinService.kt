package com.jeroenvdg.scrumdapp.db

import com.jeroenvdg.scrumdapp.models.Presence
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Checkin(
    val id: Int,
    val groupId: Int?,
    val name: String,
    val userId: Int?,
    val presence: Presence,
    val checkinStars: Int?,
    val checkupStars: Int?,
    val date: LocalDate,
    val comment: String?
)

interface CheckinService {
    suspend fun getUserCheckins(user: User, group: Group): List<Checkin>
    suspend fun getGroupCheckins(group: Group): List<Checkin>
    suspend fun getCheckin(id: Int): Checkin?
    suspend fun createCheckin(checkin: Checkin): Checkin? // Check if this is enough info for post
    suspend fun createGroupCheckin(group: Group, checkins: List<Checkin>): List<Checkin>?
    suspend fun alterCheckin(checkin: Checkin): Boolean
    suspend fun deleteCheckin(checkin: Checkin): Boolean
}