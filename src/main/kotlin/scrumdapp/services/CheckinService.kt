package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.models.Presence
import com.jeroenvdg.scrumdapp.routes.groups.clamp
import io.ktor.http.Parameters
import kotlinx.datetime.LocalDate
import kotlin.text.toIntOrNull

data class CheckinDashboardData(
    val checkins: List<Checkin>,
    val currentDate: LocalDate
)

class CheckinService(
    private val checkinRepository: CheckinRepository,
    private val groupRepository: GroupRepository,
) {
    suspend fun handleBatchCheckin(date: LocalDate, checkins: List<Checkin>, body: Parameters): Boolean {
        for (checkin in checkins) {
            checkin.date = date
            if (body.contains("checkin-${checkin.userId}")) {
                checkin.checkinStars = body["checkin-${checkin.userId}"]?.toIntOrNull()
                if (checkin.checkinStars != null) checkin.checkinStars = clamp(checkin.checkinStars!!, 0, 10)
            }
            if (body.contains("checkup-${checkin.userId}")) {
                checkin.checkupStars = body["checkup-${checkin.userId}"]?.toIntOrNull()
                if (checkin.checkupStars != null) checkin.checkupStars = clamp(checkin.checkupStars!!, 0, 10)
            }
            if (body.contains("presence-${checkin.userId}")) {
                val presneceVal = body["presence-${checkin.userId}"]?.toIntOrNull()
                checkin.presence = if (presneceVal == null) null else enumValues<Presence>()[presneceVal]
            }
            if (body.contains("comment-${checkin.userId}")) {
                checkin.comment = body["comment-${checkin.userId}"]
                if (checkin.comment.isNullOrBlank()) checkin.comment = null
            }
        }
        try {
            checkinRepository.saveGroupCheckin(checkins)
            return true
        } catch(e: Exception) {
            return false
        }

    }
}