package com.jeroenvdg.scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.middleware.userSession
import com.jeroenvdg.scrumdapp.models.Presence
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.typedGet
import com.jeroenvdg.scrumdapp.utils.typedPost
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.groups.checkinWidget
import com.jeroenvdg.scrumdapp.views.pages.groups.editableCheckinWidget
import com.jeroenvdg.scrumdapp.views.pages.groups.groupPage
import com.scrumdapp.scrumdapp.middleware.group
import com.scrumdapp.scrumdapp.middleware.groupUser
import io.ktor.server.resources.href
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receiveParameters
import io.ktor.server.routing.Route
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.application

fun Route.groupCheckinRoutes() {
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()

    typedGet<Groups.Id> { groupData ->
        val date = groupData.getIsoDateParam()
        val group = call.group
        val checkins = checkinRepository.getGroupCheckins(group.id, date)
        val userPerm = groupRepository.getGroupMemberPermissions(group.id, call.userSession.userId)
        val checkinDates = checkinRepository.getCheckinDates(group.id, 10)

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(checkinDates, group, userPerm) {
                    checkinWidget(checkins, group, date, call.groupUser.permissions)
                }
            }
        }
    }
}

fun Route.groupEditCheckinRoutes() {
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()

    typedGet<Groups.Id.Edit> { groupEditData ->
        val date = groupEditData.parent.getIsoDateParam()
        val group = call.group
        val checkins = checkinRepository.getGroupCheckins(group.id, date)
        val userPerm = groupRepository.getGroupMemberPermissions(group.id, call.userSession.userId)
        val checkinDates = checkinRepository.getCheckinDates(group.id, 10)

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(checkinDates, group, userPerm) {
                    editableCheckinWidget(checkins, group, date)
                }
            }
        }
    }

    typedPost<Groups.Id.Edit> { groupEditData ->
        val date = groupEditData.parent.getIsoDateParam()
        val group = call.group
        val checkins = checkinRepository.getGroupCheckins(group.id, date)
        val body = call.receiveParameters()

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

        checkinRepository.saveGroupCheckin(checkins)
        call.respondRedirect(application.href(Groups.Id(groupId=group.id, date=groupEditData.parent.date)))
    }
}