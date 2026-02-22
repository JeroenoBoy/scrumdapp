package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.GroupUser
import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.models.Presence
import com.jeroenvdg.scrumdapp.routes.groups.clamp
import com.jeroenvdg.scrumdapp.utils.now
import com.jeroenvdg.scrumdapp.utils.parseMonth
import io.ktor.http.Parameters
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.toKotlinLocalDate
import java.time.YearMonth
import kotlin.text.toIntOrNull

data class CheckinDashboardData(
    val checkins: List<Checkin>,
    val currentDate: LocalDate
)

data class MonthData(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val yearMonth: YearMonth,
    val checkinDays: List<CheckinDay>,
)

data class CheckinDay(
    val date: LocalDate,
    val hasCheckin: Boolean,
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
                val presenceVal = body["presence-${checkin.userId}"]?.toIntOrNull()
                checkin.presence = if (presenceVal == null) null else enumValues<Presence>()[presenceVal]
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

    suspend fun handleGroupPresence(date: LocalDate, checkins: List<Checkin>, body: Parameters) {
        for (checkin in checkins) {
            checkin.date = date
            if (body.contains("presence-${checkin.userId}")) {
                val presenceVal = body["presence-${checkin.userId}"]?.toIntOrNull()
                checkin.presence = if (presenceVal == null) null else enumValues<Presence>()[presenceVal]
            }
        }

        try {
           checkinRepository.saveGroupCheckin(checkins)
        } catch(e: Exception) {
            throw ServerFaultException()
        }
    }

    suspend fun handleUserCheckin(user: User, group: Group, date: LocalDate, body: Parameters) {
        val checkin: Int? = if (body.contains("checkin")) {
            val v = body["checkin"]?.toIntOrNull()
            if (v != null) { clamp(v, 0, 10) } else null
        } else null

        val checkup: Int? = if (body.contains("checkup")) {
            val v = body["checkup"]?.toIntOrNull()
            if (v != null) { clamp(v, 0, 10) } else null
        } else null

        val comment: String? = if (body.contains("comment")) {
            body["comment"]
        } else null

        checkinRepository.saveUserCheckin(user.id, group.id, date, checkin, checkup, comment)
    }

    suspend fun handleUserCheckin(checkin: Checkin, body: Parameters) {
        if (body.contains("checkin")) {
            checkin.checkinStars = body["checkin"]?.toIntOrNull()
            if (checkin.checkinStars != null) checkin.checkinStars = clamp(checkin.checkinStars!!, 0, 10)
        }
        if (body.contains("checkup")) {
            checkin.checkupStars = body["checkup"]?.toIntOrNull()
            if (checkin.checkupStars != null) checkin.checkupStars = clamp(checkin.checkupStars!!, 0, 10)
        }
        if (body.contains("presence")) {
            val presenceVal = body["presence"]?.toIntOrNull()
            checkin.presence = if (presenceVal == null) null else enumValues<Presence>()[presenceVal]
        }
        if (body.contains("comment")) {
            checkin.comment = body["comment"]
            if (checkin.comment.isNullOrBlank()) checkin.comment = null
        }

        checkinRepository.saveGroupCheckin(listOf(checkin))
    }

    suspend fun getMonthlyDates(groupId: Int, month: String? = null, year: Int? = null): MonthData {
        val today = LocalDate.now()
        val parsedMonth = if (month != null) parseMonth(month) else today.month
        val parsedYear = year ?: today.year
        val yearMonth = YearMonth.of(parsedYear, parsedMonth.value)
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        val weekStartDate = startDate.minusDays(startDate.dayOfWeek.value - 1L).toKotlinLocalDate()
        val weekEndDate = endDate.plusDays(7L - endDate.dayOfWeek.value).toKotlinLocalDate()

        val days = mutableListOf<CheckinDay>()
        val checkinData = checkinRepository.getDatesBetween(groupId, weekStartDate, weekEndDate)

        var checkinDataI = 0
        var currentDate = weekStartDate
        var i = 0
        while (currentDate <= weekEndDate) {
            // ensures always at the same day
            while (checkinDataI < checkinData.size && currentDate > checkinData[checkinDataI]) {
                checkinDataI++
            }
            days.add(CheckinDay(currentDate, checkinDataI < checkinData.size && currentDate == checkinData[checkinDataI]))
            i++
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        }

        return MonthData(weekStartDate, weekEndDate, yearMonth, days)
    }

}