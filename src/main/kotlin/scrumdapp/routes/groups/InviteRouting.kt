package com.jeroenvdg.scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.user
import com.jeroenvdg.scrumdapp.middleware.userSession
import com.jeroenvdg.scrumdapp.services.EncryptionService
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.invitationpage
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

suspend fun Application.configureInviteRoutes() {
    val groupRepository = dependencies.resolve<GroupRepository>()
    val encryptionService = dependencies.resolve<EncryptionService>()

    val tokenLength = 60
    val tokenRegex = Regex("^[A-Za-z0-9]{$tokenLength}")

    routing {
        route("/invitations") {
            install(IsLoggedIn)

            get {
                val token = call.queryParameters["token"]

                if (token.isNullOrBlank()) {
                    return@get call.respondRedirect("/home")
                }
                val invite = groupRepository.getGroupInvite(token) ?: return@get call.respondRedirect("/home")
                val group = groupRepository.getGroup(invite.groupId) ?: return@get call.respondRedirect("/home")

                call.respondHtml {
                    dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                        invitationpage(group, token)
                    }
                }
            }

            post {
                val userId = call.userSession.userId
                val token = call.queryParameters["token"]
                val password = call.receiveParameters()["group_password"]

                if (token.isNullOrBlank() || password.isNullOrBlank()) {
                    return@post call.respondRedirect("/invitations?token=$token#password-failure")
                }

                val invite = groupRepository.getGroupInvite(token) ?: return@post call.respondRedirect("/invitations#password-failure?token=$token")
                val groupUsers = groupRepository.getGroupUsers(invite.groupId)

                if (encryptionService.compareHash(password, invite.password?: "")) {
                    if (groupUsers.any { it.userId == call.user.id}) {
                        return@post call.respondRedirect("/groups/${invite.groupId}")
                    }

                    groupRepository.addGroupMember(invite.groupId, userId)
                    call.respondRedirect("/groups/${invite.groupId}")
                } else {
                    call.respondRedirect("/invitations?token=$token#password-mistake")
                }
            }
        }
    }
}