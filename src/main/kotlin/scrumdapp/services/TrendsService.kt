package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.models.Presence
import com.jeroenvdg.scrumdapp.utils.YearWeek
import com.jeroenvdg.scrumdapp.utils.now
import kotlinx.datetime.LocalDate
import kotlin.math.max
import kotlin.math.min

data class TrendsData(val trends: List<TrendData>, val highest: Int, var from: LocalDate, var to: LocalDate) : Iterable<TrendData> {
    val size get() = trends.size
    operator fun get(i: Int) = trends[i]
    override fun iterator() = trends.iterator()
}

data class TrendData(val userId: Int, val userName: String, val groupId: Int) {
    var onTimeCount: Int = 0
    var lateCount: Int = 0
    var verifiedAbsentCount: Int = 0
    var absentCount: Int = 0
    var sickCount: Int = 0

    fun push(presence: Presence) {
        when(presence) {
            Presence.OnTime -> onTimeCount++
            Presence.Late -> lateCount++
            Presence.VerifiedAbsent -> verifiedAbsentCount++
            Presence.Absent -> absentCount++
            Presence.Sick -> sickCount++
        }
    }

    fun total() = onTimeCount + lateCount + verifiedAbsentCount + absentCount + sickCount

    operator fun get(presence: Presence) : Int {
        return when(presence) {
            Presence.OnTime -> onTimeCount
            Presence.Late -> lateCount
            Presence.VerifiedAbsent -> verifiedAbsentCount
            Presence.Absent -> absentCount
            Presence.Sick -> sickCount
        }
    }
}

data class WeeklyStarData(val date: YearWeek, val checkin: AverageStars, val checkup: AverageStars)
data class AverageStars(val min: Int?, val max: Int?, val avg: Float)

class TrendsService(
    val checkinRepository: CheckinRepository,
) {
    suspend fun getRecentData(groupId: Int): TrendsData {
        val to = LocalDate.now()
        val from = LocalDate.fromEpochDays(to.toEpochDays() - 14)
        return getTrendsData(groupId, from, to)
    }

    suspend fun getAllTrendsData(groupId: Int): TrendsData {
        val to = LocalDate.now()
        val from = LocalDate.fromEpochDays(0)
        return getTrendsData(groupId, from, to)
    }

    suspend fun getTrendsData(groupId: Int, from: LocalDate, to: LocalDate): TrendsData {
        val checkins = checkinRepository.getPresenceBetween(groupId, from, to)
        val data = hashMapOf<Int, TrendData>() // UserId : TrendsData
        var highest = 0
        for (presenceData in checkins) {
            if (!data.containsKey(presenceData.userId)) {
                data[presenceData.userId] = TrendData(presenceData.userId, presenceData.userName, presenceData.groupId)
            }
            if (presenceData.presence != null) {
                val d = data[presenceData.userId]?: continue
                d.push(presenceData.presence!!)
                highest = max(highest, d.total())
            }
        }
        return TrendsData(data.values.sortedBy { it.userName }, highest, from, to)
    }

    suspend fun getUserCheckins(userId: Int, groupId: Int): List<Checkin> {
        return checkinRepository.getUserCheckins(userId, groupId)
    }

    suspend fun getUserCheckins(userId: Int, groupId: Int, from: LocalDate, to: LocalDate): List<Checkin> {
        return checkinRepository.getUserCheckins(userId, groupId, from, to)
    }

    fun getWeeklyStarsData(sortedCheckins: List<Checkin>): List<WeeklyStarData> {
        val checkins = mutableListOf<WeeklyStarData>()

        var currDate = YearWeek.of(0, 0)
        var checkinMin: Int? = null
        var checkinMax: Int? = null
        var checkinTotal = 0
        var checkinCount = 0
        var checkupMin: Int? = null
        var checkupMax: Int? = null
        var checkupTotal = 0
        var checkupCount = 0
        var totalCount = 0

        fun nextDate(date: YearWeek) {
            if (totalCount > 0) {
                checkins.add(WeeklyStarData(
                    currDate,
                    AverageStars(checkinMin, checkinMax, checkinTotal.toFloat() / checkinCount.toFloat()),
                    AverageStars(checkupMin, checkupMax, checkupTotal.toFloat() / checkupCount.toFloat())))
            }
            currDate = date
            totalCount = 0
            checkinMin = 0
            checkinMax = 0
            checkinTotal = 0
            checkinCount = 0
            checkupMin = 0
            checkupMax = 0
            checkupTotal = 0
            checkupCount = 0
        }

        for (checkin in sortedCheckins) {
            val date = YearWeek.of(checkin.date)
            if (currDate != date) {
                nextDate(date)
            }

            totalCount++
            if (checkin.checkinStars != null) {
                val stars = checkin.checkupStars!!
                checkinMin = if (checkinMin == null) stars else min(stars, checkinMin)
                checkinMax = if (checkinMax == null) stars else max(stars, checkinMax)
                checkinTotal += stars
                checkinCount++
            }
            if (checkin.checkupStars != null) {
                val stars = checkin.checkupStars!!
                checkupMin = if (checkupMin == null) stars else min(stars, checkupMin)
                checkupMax = if (checkupMax == null) stars else max(stars, checkupMax)
                checkupTotal += stars
                checkupCount++
            }
        }

        return checkins
    }
}