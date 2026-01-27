package com.jeroenvdg.scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.middleware.group
import com.jeroenvdg.scrumdapp.middleware.groupUser
import com.jeroenvdg.scrumdapp.services.CheckinService
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.route
import com.jeroenvdg.scrumdapp.utils.typedGet
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.PageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.mainLayout
import com.jeroenvdg.scrumdapp.views.pages.groups.calendarContent
import com.jeroenvdg.scrumdapp.views.pages.groups.contentFrame
import com.jeroenvdg.scrumdapp.views.pages.groups.groupPage
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.resources.href
import io.ktor.server.routing.Route
import io.ktor.server.routing.application

fun Route.calendarRoutes() {
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()
    val checkinService = application.dependencies.resolveBlocking<CheckinService>()

    typedGet<GroupsRouter.Group.Calendar> { calendarData ->
        val group = call.group
        val groupUser = call.groupUser
        val checkinDates = checkinRepository.getRecentCheckinDates(group.id)

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(application, checkinDates, group, groupUser.permissions) {
                    contentFrame(application.href(GroupsRouter.Group.Calendar.Content(calendarData)))
                }
            }
        }
    }

    route<GroupsRouter.Group.Calendar.Content> {
        typedGet<GroupsRouter.Group.Calendar.Content> { calendarContentData ->
            val group = call.group
            val dates = checkinService.getMonthlyDates(group.id, calendarContentData.month, calendarContentData.year)
            val possibleMonths = checkinRepository.getDistinctMonths(group.id)

            call.respondHtml {
                mainLayout(PageData("Calendar frame", contentFrame=true)) {
                    calendarContent(application, group, possibleMonths, dates)
                }
            }
        }
    }
}