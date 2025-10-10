package com.jeroenvdg.scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.middleware.group
import com.jeroenvdg.scrumdapp.middleware.groupUser
import com.jeroenvdg.scrumdapp.utils.href
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.route
import com.jeroenvdg.scrumdapp.utils.typedGet
import com.jeroenvdg.scrumdapp.utils.typedPost
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.groups.groupConfigContent
import com.jeroenvdg.scrumdapp.views.pages.groups.groupPage
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receiveParameters
import io.ktor.server.resources.href
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.application

fun Route.groupSettingsRoutes() {
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()
    val nameRegex = Regex("^[a-zA-Z0-9_ ]{3,50}$")

    typedGet<GroupsRouter.Id.Settings> { settingsParams ->
        val group = call.group
        val groupUser = call.groupUser
        val checkinDates = checkinRepository.getCheckinDates(group.id, 10)

        call.respondHtml {
            dashboardLayout(DashboardPageData("Settings", call, group.bannerImage)) {
                groupPage(application, checkinDates, group, groupUser.permissions) {
                    groupConfigContent(application, group, groupUser, backgrounds)
                }
            }
        }
    }

    route<GroupsRouter.Id.Settings.ChangeName> {
        typedPost<GroupsRouter.Id.Settings.ChangeName> { changeNameRouteParams ->
            val name = call.receiveParameters()["group_name"]
            val group = call.group
            if (name == null) { return@typedPost call.respondRedirect(application.href(GroupsRouter.Id.Settings(group.id))) }
            if (name == call.group.name) { return@typedPost call.respondRedirect(application.href(GroupsRouter.Id.Settings(group.id))) }
            if (!nameRegex.matches(name)) { return@typedPost call.respondRedirect(application.href(GroupsRouter.Id.Settings(group.id))) }

            groupRepository.updateGroup(group.id, name=name)
            call.respondRedirect(application.href(GroupsRouter.Id.Settings(group.id)))
        }
    }

    route<GroupsRouter.Id.Settings.ChangeBackground> {
        typedPost<GroupsRouter.Id.Settings.ChangeBackground> {
            val bannerImage = call.receiveParameters()["img"]
            val group = call.group
            if (!backgrounds.contains(bannerImage)) { return@typedPost call.respondRedirect(application.href(GroupsRouter.Id.Settings(group.id))) }
            if (bannerImage == null) { return@typedPost call.respondRedirect(application.href(GroupsRouter.Id.Settings(group.id))) }
            if (bannerImage == call.group.bannerImage) { return@typedPost call.respondRedirect(application.href(GroupsRouter.Id.Settings(group.id))) }

            groupRepository.updateGroup(group.id, bannerImage = bannerImage)
            call.respondRedirect(application.href(GroupsRouter.Id.Settings(group.id)))
        }
    }

    route<GroupsRouter.Id.Settings.Delete> {
        typedPost<GroupsRouter.Id.Settings.Delete> {
            val name = call.receiveParameters()["delete_group_name"]
            val group = call.group
            if (name != call.group.name) {
                return@typedPost call.respondRedirect(application.href(GroupsRouter.Id.Settings(group.id), "delete-failed"))
            }

            groupRepository.deleteGroup(group.id)
            call.respondRedirect("/home")
        }
    }
}
