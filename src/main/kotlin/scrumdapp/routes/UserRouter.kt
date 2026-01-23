package com.jeroenvdg.scrumdapp.routes

import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.UserRepository
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.user
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.services.ServerFaultException
import com.jeroenvdg.scrumdapp.utils.href
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.route
import com.jeroenvdg.scrumdapp.utils.typedGet
import com.jeroenvdg.scrumdapp.utils.typedPost
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.appSettingsPage
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receiveParameters
import io.ktor.server.resources.href
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.routing

@Resource("appsettings")
class UserSettingsRouter {
    @Resource("delete")
    class Delete(val parent: UserSettingsRouter = UserSettingsRouter()) {

    }
}

fun Application.configureAppSettingsRoutes() {
    routing {
        route<UserSettingsRouter> {
            install(IsLoggedIn)
            userSettingsRouter()
        }
    }
}

fun Route.userSettingsRouter() {
    val userRepository = application.dependencies.resolveBlocking<UserRepository>()
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()
    typedGet<UserSettingsRouter> {
        val user = call.user

        call.respondHtml {
            dashboardLayout(DashboardPageData("Gebruikers instellingen", call)) {
                appSettingsPage(application, user)
            }
        }
    }

    route<UserSettingsRouter.Delete> {
        typedPost<UserSettingsRouter.Delete> {
            val name = call.receiveParameters()["delete_user_name"]

            if (name.isNullOrEmpty()) return@typedPost call.respondRedirect(application.href(UserSettingsRouter(), "delete-user-failed"))

            val user = call.user
            if (!name.equals(user.name, true)) return@typedPost call.respondRedirect(application.href(UserSettingsRouter(), "delete-user-failed"))

            try {
                val groups = groupRepository.getUserGroups(user.id)

                for (group in groups) {
                    val userGroup = groupRepository.getGroupUser(group.id, user.id) ?: continue
                    if (userGroup.permissions == UserPermissions.LordOfScrum ) {
                        val groupUsers = groupRepository.getGroupUsers(group.id)

                        if (groupUsers.size <= 1) {
                            groupRepository.deleteGroup(userGroup.groupId)
                            continue
                        }
                        val newOwner = groupUsers.filter { it.user != user }.minByOrNull { it.permissions.id }!!
                        groupRepository.alterGroupMemberPerms(userGroup.groupId, newOwner.user.id, UserPermissions.LordOfScrum)
                    }

                    groupRepository.deleteGroupMember(userGroup.groupId, user.id)
                }
            } catch (ex: Exception) {
                throw ServerFaultException("Er is iets misgegaan met het verwijderen van de gebruiker")
            }

            userRepository.deleteUser(user)

            return@typedPost call.respondRedirect(application.href(UserSettingsRouter()))
        }
    }
}