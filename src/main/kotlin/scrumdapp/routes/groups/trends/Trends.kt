package com.jeroenvdg.scrumdapp.routes.groups.trends

import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.GroupUser
import com.jeroenvdg.scrumdapp.middleware.group
import com.jeroenvdg.scrumdapp.middleware.groupUser
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.services.CheckinService
import com.jeroenvdg.scrumdapp.services.TrendsData
import com.jeroenvdg.scrumdapp.services.TrendsService
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.typedGet
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.groups.groupPage
import com.jeroenvdg.scrumdapp.views.pages.groups.trends.groupExportContent
import com.jeroenvdg.scrumdapp.views.pages.groups.trends.groupTrendsContent
import io.ktor.server.html.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate

fun Route.groupTrendsRoutes() {
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()
    val checkinService = application.dependencies.resolveBlocking<CheckinService>()
    val trendsService = application.dependencies.resolveBlocking<TrendsService>()

    typedGet<GroupsRouter.Group.Trends> { trends ->
        val group = call.group
        val groupUser = call.groupUser
        val users: List<GroupUser> = groupRepository.getGroupUsers(group.id);
        val checkinDates: List<LocalDate> = checkinRepository.getRecentCheckinDates(group.id)

        val view = trends.view ?: "1"
        val trendsData: TrendsData = if (view == "all") {
            trendsService.getAllTrendsData(group.id)
        } else {
            trendsService.getRecentData(group.id)
        }

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(application, checkinDates, group, groupUser.permissions) {
                    groupTrendsContent(application, group, trendsData, view)
                    groupExportContent(application, groupUser, users)
                }
            }
        }
    }

//    route<GroupsRouter.Group.Trends.User> {
//        typedGet<GroupsRouter.Group.Trends.User> { userTrends ->
//            val group = call.group
//            val groupUser = call.groupUser
//            val checkinDates = checkinRepository.getCheckinDates(group.id, 10)
//            val targetUser = groupRepository.getGroupUser(userTrends.parent.parent.groupId, userTrends.userId)
//                ?: return@typedGet call.respondRedirect(application.href(GroupsRouter.Group.Trends(group.id)))
//
//            val view = userTrends.parent.view ?: "all"
//            call.respondHtml {
//                dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
//                    groupPage(application, checkinDates, group, groupUser.permissions) {
//                        userTrendsContent(application, targetUser, view)
//                    }
//                }
//            }
//        }
//    }
}