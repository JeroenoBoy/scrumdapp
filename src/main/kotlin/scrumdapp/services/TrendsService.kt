package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.models.Presence
import com.jeroenvdg.scrumdapp.utils.now
import kotlinx.datetime.LocalDate
import kotlin.math.max

data class TrendsData(val trends: List<TrendData>, val highest: Int, val from: LocalDate, val to: LocalDate) : Iterable<TrendData> {
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

class TrendsService(
    val checkinRepository: CheckinRepository,
) {
    suspend fun getRecentData(groupId: Int): TrendsData {
        val to = LocalDate.now()
        val from = LocalDate.fromEpochDays(to.toEpochDays() - 14)
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
}