package com.jeroenvdg.scrumdapp.routes.groups.trends

import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.middleware.group
import com.jeroenvdg.scrumdapp.middleware.groupUser
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.services.CheckinService
import com.jeroenvdg.scrumdapp.services.TrendsService
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.typedGet
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.groups.groupPage
import com.jeroenvdg.scrumdapp.views.pages.groups.trends.groupTrendsContent
import io.ktor.server.html.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*

fun Route.groupTrendsRoutes() {
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()
    val checkinService = application.dependencies.resolveBlocking<CheckinService>()
    val trendsService = application.dependencies.resolveBlocking<TrendsService>()

    typedGet<GroupsRouter.Group.Trends> { trends ->
        val group = call.group
        val groupUser = call.groupUser
        val checkinDates = checkinRepository.getCheckinDates(group.id, 10)
        val trendsData = trendsService.getRecentData(group.id)

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(application, checkinDates, group, groupUser.permissions) {
                    groupTrendsContent(trendsData)
                }
            }
        }
    }
}