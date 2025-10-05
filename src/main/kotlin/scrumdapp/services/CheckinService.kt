package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.GroupRepository
import kotlinx.datetime.LocalDate

data class CheckinDashboardData(
    val checkins: List<Checkin>,
    val currentDate: LocalDate
)

class CheckinService(
    private val checkinRepository: CheckinRepository,
    private val groupRepository: GroupRepository,
) {

    suspend fun getDailyCheckin(groupId: Int, userId: Int, dataParam: String) {

    }
}