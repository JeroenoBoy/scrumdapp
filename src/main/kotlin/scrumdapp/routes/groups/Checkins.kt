package com.jeroenvdg.scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.middleware.group
import com.jeroenvdg.scrumdapp.middleware.groupUser
import com.jeroenvdg.scrumdapp.middleware.user
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

fun Route.groupCheckinRoutes() {
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()

    typedGet<GroupsRouter.Group> { groupData ->
        val date = groupData.getIsoDateParam()
        val group = call.group
        val groupUser = call.groupUser
        val checkins = checkinRepository.getGroupCheckins(group.id, date)
        val checkinDates = checkinRepository.getRecentCheckinDates(group.id)

        call.respondHtml {
            dashboardLayout(application, DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(application, checkinDates, group, groupUser.permissions) {
                    checkinWidget(application, groupUser, checkins, group, date)
                }
            }
        }
    }
}

fun Route.groupEditCheckinRoutes() {
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()
    val checkinService = application.dependencies.resolveBlocking<CheckinService>()

    typedGet<GroupsRouter.Group.Edit> { groupEditData ->
        val date = groupEditData.parent.getIsoDateParam()
        val group = call.group
        val checkins = checkinRepository.getGroupCheckins(group.id, date)
        val checkinDates = checkinRepository.getRecentCheckinDates(group.id)

        call.respondHtml {
            dashboardLayout(application, DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(application, checkinDates, group, call.groupUser.permissions) {
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
            val checkinDates = checkinRepository.getRecentCheckinDates(group.id)
            return@typedPost call.respondHtml {
                dashboardLayout(application, DashboardPageData(group.name, call, group.bannerImage)) {
                    groupPage(application, checkinDates, group, call.groupUser.permissions, ValidationException().toExceptionContent()) {
                        editableCheckinWidget(application, checkins, group, date)
                    }
                }
            }
        }

        call.respondRedirect(application.href(GroupsRouter.Group(groupId=group.id, date=groupEditData.parent.date)))
    }

    route<GroupsRouter.Group.Edit.User> {
        typedPost<GroupsRouter.Group.Edit.User> { checkinEditData ->
            val date = checkinEditData.parent.parent.getIsoDateParam()
            val user = call.user
            val group = call.group

            if (checkinEditData.userId != user.id) {
                throw NoAccessException("Je kan alleen je eigen check-ins editen")
            }

            val checkin = checkinRepository.getUserCheckin(user.id, group.id, date) ?: Checkin(group.id, user.id, date)
            checkinService.handleUserCheckin(checkin, call.receiveParameters())
            call.respondRedirect(application.href(GroupsRouter.Group(groupId=group.id, date=checkinEditData.parent.parent.date)))
        }
    }
}