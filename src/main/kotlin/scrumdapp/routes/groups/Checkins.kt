package com.jeroenvdg.scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.middleware.group
import com.jeroenvdg.scrumdapp.middleware.groupUser
import com.jeroenvdg.scrumdapp.middleware.userSession
import com.jeroenvdg.scrumdapp.models.Presence
import com.jeroenvdg.scrumdapp.services.AppException
import com.jeroenvdg.scrumdapp.services.CheckinService
import com.jeroenvdg.scrumdapp.services.NoAccessException
import com.jeroenvdg.scrumdapp.services.ValidationException
import com.jeroenvdg.scrumdapp.services.toExceptionContent
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.route
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
import io.ktor.server.routing.route

fun Route.groupCheckinRoutes() {
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()

    typedGet<GroupsRouter.Id> { groupData ->
        val date = groupData.getIsoDateParam()
        val group = call.group
        val checkins = checkinRepository.getGroupCheckins(group.id, date)
        val userPerm = call.groupUser.permissions
        val checkinDates = checkinRepository.getCheckinDates(group.id, 10)

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(application, checkinDates, group, userPerm) {
                    checkinWidget(application, checkins, group, date, userPerm)
                }
            }
        }
    }
}

fun Route.groupEditCheckinRoutes() {
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()
    val checkinService = application.dependencies.resolveBlocking<CheckinService>()

    typedGet<GroupsRouter.Id.Edit> { groupEditData ->
        val date = groupEditData.parent.getIsoDateParam()
        val group = call.group
        val checkins = checkinRepository.getGroupCheckins(group.id, date)
        val checkinDates = checkinRepository.getCheckinDates(group.id, 10)

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(application, checkinDates, group, call.groupUser.permissions) {
                    editableCheckinWidget(application, checkins, group, date)
                }
            }
        }
    }

    typedPost<GroupsRouter.Id.Edit> { groupEditData ->
        val date = groupEditData.parent.getIsoDateParam()
        val group = call.group
        val checkins = checkinRepository.getGroupCheckins(group.id, date)
        val success = checkinService.handleBatchCheckin(date, checkins, call.receiveParameters())

        if (!success) {
            val checkinDates = checkinRepository.getCheckinDates(group.id, 10)
            return@typedPost call.respondHtml {
                dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                    groupPage(application, checkinDates, group, call.groupUser.permissions, ValidationException().toExceptionContent()) {
                        editableCheckinWidget(application, checkins, group, date)
                    }
                }
            }
        }

        call.respondRedirect(application.href(GroupsRouter.Id(groupId=group.id, date=groupEditData.parent.date)))
    }

    route<GroupsRouter.Id.Edit.Delete> {
        typedPost<GroupsRouter.Id.Edit.Delete> { groupEditData ->
            val group = call.group
            val date = groupEditData.parent.parent.getIsoDateParam()
            try {
                val checkin = checkinRepository.getGroupCheckins(group.id, date)
                if (checkin.isNotEmpty()) {checkinRepository.deleteCheckins(checkin)}
            } catch (e: Exception) {
                // To Do: integrate this with new error handling from last pr.
                call.respondRedirect(application.href(GroupsRouter.Id(groupId=group.id)))
            }
            call.respondRedirect(application.href(GroupsRouter.Id(groupId=group.id)))
        }
    }

}