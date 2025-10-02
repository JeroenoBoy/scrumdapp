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
    var presence: Presence?,
    var checkinStars: Int?,
    var checkupStars: Int?,
    var date: LocalDate,
    var comment: String?
)

interface CheckinService {
    suspend fun getUserCheckins(user: User, groupId: Int): List<Checkin>
    suspend fun getGroupCheckins(groupId: Int, date: LocalDate): List<Checkin>
    suspend fun getCheckinDates(groupId: Int, limit: Int): List<LocalDate>
    suspend fun getCheckin(id: Int): Checkin?
    suspend fun createCheckin(checkin: Checkin): Checkin? // Check if this is enough info for post
    suspend fun createGroupCheckin(groupId: Int, checkins: List<Checkin>): List<Checkin>?
    suspend fun saveGroupCheckin(checkins: List<Checkin>)
    suspend fun alterCheckin(checkin: Checkin): Boolean
    suspend fun deleteCheckin(checkin: Checkin): Boolean
}