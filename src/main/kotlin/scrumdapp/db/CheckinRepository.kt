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

data class PresenceData(
    val checkinId: Int,
    val groupId: Int,
    val userId: Int,
    val userName: String,
    var presence: Presence?,
    var date: LocalDate,
)

interface CheckinRepository {
    suspend fun getUserCheckins(user: User, groupId: Int): List<Checkin>
    suspend fun getGroupCheckins(groupId: Int, date: LocalDate): List<Checkin>
    suspend fun getCheckinDates(groupId: Int, limit: Int): List<LocalDate>
    suspend fun getCheckin(id: Int): Checkin?
    suspend fun createCheckin(checkin: Checkin): Checkin? // Check if this is enough info for post
    suspend fun createGroupCheckin(groupId: Int, checkins: List<Checkin>): List<Checkin>?
    suspend fun saveGroupCheckin(checkins: List<Checkin>)
    suspend fun alterCheckin(checkin: Checkin): Boolean
    suspend fun deleteCheckin(checkin: Checkin): Boolean

    suspend fun getPresenceBetween(groupId: Int, from: LocalDate, to: LocalDate): List<PresenceData>
    suspend fun getDatesBetween(groupId: Int, from: LocalDate, to: LocalDate): List<LocalDate>
}