package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.GroupRepository
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
import kotlin.math.min
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

    suspend fun getMonthlyDates(groupId: Int, month: String? = null, year: Int? = null): MonthData {
        val today = LocalDate.now()
        val parsedMonth = if (month != null) parseMonth(month) else today.month
        val parsedYear = year ?: today.year
        val yearMonth = YearMonth.of(parsedYear, parsedMonth.value)
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        val weekStartDate = startDate.minusDays(startDate.dayOfWeek.value - 1L).toKotlinLocalDate()
        val weekEndDate = endDate.plusDays(7L - startDate.dayOfWeek.value - 1L).toKotlinLocalDate()

        val days = mutableListOf<CheckinDay>()
        val checkinData = checkinRepository.getDatesBetween(groupId, weekStartDate, weekEndDate)

        var checkinDataI = 0
        var currentDate = weekStartDate
        var i = 0
        while (currentDate <= weekEndDate) {
            // ensures always at the same day
            while (currentDate > checkinData[checkinDataI] && checkinDataI != checkinData.size - 1) {
                checkinDataI = min(checkinData.size - 1, checkinDataI + 1)
            }
            days.add(CheckinDay(currentDate, currentDate == checkinData[checkinDataI]))
            i++
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        }

        return MonthData(weekStartDate, weekEndDate, yearMonth, days)
    }
}