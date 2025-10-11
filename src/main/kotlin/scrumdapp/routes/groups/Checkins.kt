package com.jeroenvdg.scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.middleware.group
import com.jeroenvdg.scrumdapp.middleware.groupUser
import com.jeroenvdg.scrumdapp.middleware.userSession
import com.jeroenvdg.scrumdapp.services.CheckinService
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.typedGet
import com.jeroenvdg.scrumdapp.utils.typedPost
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.groups.checkinWidget
import com.jeroenvdg.scrumdapp.views.pages.groups.editableCheckinWidget
import com.jeroenvdg.scrumdapp.views.pages.groups.groupPage
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

    typedGet<GroupsRouter.Group> { groupData ->
        val date = groupData.getIsoDateParam()
        val group = call.group
        val groupUser = call.groupUser
        val checkins = checkinRepository.getGroupCheckins(group.id, date)
        val checkinDates = checkinRepository.getCheckinDates(group.id, 10)

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(application, checkinDates, group, groupUser.permissions) {
                    checkinWidget(application, checkins, group, date, call.groupUser.permissions)
                }
            }
        }
    }
}

fun Route.groupEditCheckinRoutes() {
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()
    val checkinService = application.dependencies.resolveBlocking<CheckinService>()

    typedGet<GroupsRouter.Group.Edit> { groupEditData ->
        val date = groupEditData.parent.getIsoDateParam()
        val group = call.group
        val checkins = checkinRepository.getGroupCheckins(group.id, date)
        val userPerm = groupRepository.getGroupMemberPermissions(group.id, call.userSession.userId)
        val checkinDates = checkinRepository.getCheckinDates(group.id, 10)

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(application, checkinDates, group, userPerm) {
                    editableCheckinWidget(application, checkins, group, date)
                }
            }
        }
    }

    typedPost<GroupsRouter.Group.Edit> { groupEditData ->
        val date = groupEditData.parent.getIsoDateParam()
        val group = call.group
        val checkins = checkinRepository.getGroupCheckins(group.id, date)
        val success = checkinService.handleBatchCheckin(date, checkins, call.receiveParameters())

        if (!success) {
            // TO DO: Handle this
            return@typedPost call.respondRedirect(application.href(GroupsRouter.Group(groupId=group.id, date=groupEditData.parent.date)))
        }

        call.respondRedirect(application.href(GroupsRouter.Group(groupId=group.id, date=groupEditData.parent.date)))
    }
}