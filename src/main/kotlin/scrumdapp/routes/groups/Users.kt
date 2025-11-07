package com.jeroenvdg.scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.middleware.group
import com.jeroenvdg.scrumdapp.middleware.groupUser
import com.jeroenvdg.scrumdapp.services.GroupService
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.route
import com.jeroenvdg.scrumdapp.utils.typedGet
import com.jeroenvdg.scrumdapp.utils.typedPost
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.groups.groupPage
import com.jeroenvdg.scrumdapp.views.pages.groups.userEditContent
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receiveParameters
import io.ktor.server.resources.href
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.application

fun Route.groupUserRoutes() {
    val groupService = application.dependencies.resolveBlocking<GroupService>()
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()

    typedGet<GroupsRouter.Group.Users> { groupUserParams ->
        val group = call.group
        val groupUser = call.groupUser
        val checkinDates = checkinRepository.getCheckinDates(group.id, 10)
        val groupUsers = groupRepository.getGroupUsers(group.id)

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(application, checkinDates, group, groupUser.permissions) {
                    userEditContent(application, groupUser, group, groupUsers)
                }
            }
        }
    }

    typedPost<GroupsRouter.Group.Users> { groupUserParams ->
        val params = call.receiveParameters()
        val permChanges = params.entries()
            .filter { it.key.startsWith("role-") }
            .mapNotNull { entry ->
                val userId = entry.key.removePrefix("role-").toIntOrNull()
                val permId = entry.value.firstOrNull()?.toIntOrNull()
                if (userId != null && permId != null) userId to permId else null
            }
            .toMap()
        val success = groupService.alterUserPermissions(call.group.id, permChanges, call.groupUser)
        val response = if (success) {
            "alter-success"
        } else {
            "alter-failed"
        }
        call.respondRedirect("/groups/${call.group.id}/users#$response")
    }

    route<GroupsRouter.Group.Users.Delete> {
        typedPost<GroupsRouter.Group.Users.Delete> { deleteParams ->
            val userId = deleteParams.userId
            val group = call.group

            val success = groupService.deleteUserFromGroup(group.id, userId, call.groupUser.permissions)
            if (!success) {
                return@typedPost call.respondRedirect(application.href(GroupsRouter.Group.Users(group.id)))
            }

            call.respondRedirect(application.href(GroupsRouter.Group.Users(group.id)))
        }
    }
}

//                route("/users") {
//                    install(HasCorrectPerms) { permissions = UserPermissions.UserManagement }
//
//                    post("/create-invite") {
//                        val passwordRegex = Regex("^[a-zA-Z0-9_ .,#^!?><]{3,50}")
//                        val group = call.group
//                        val token = generateRandomToken(60)
//                        val password = call.receiveParameters()["create_group_invite"]
//
//                        if (password.isNullOrBlank() || !passwordRegex.matches(password)) {
//                            return@post call.respondRedirect("/groups/${group.id}/users#create-invite")
//                        } else {
//                            try {
//                                groupRepository.createGroupInvite(group.id, token, encryptionService.hashValue(password))
//                            } catch (e: Exception) {
//                                print(e.message)
//                            }
//                        }
//
//                        val origin = call.request.origin.serverHost
//                        val url = "https://$origin/invitations?token=$token"
//
//                        call.respondHtml {
//                            dashboardLayout(DashboardPageData(group.name, call)) {
//                                groupPage(emptyList(), group, call.groupUser.permissions) {
//                                    userInviteContent(group, url)
//                                }
//                            }
//                        }
//                    }
//                }
