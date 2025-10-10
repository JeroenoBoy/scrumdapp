package com.jeroenvdg.scrumdapp.routes.invites

import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.middleware.userSession
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.services.InviteService
import com.jeroenvdg.scrumdapp.utils.href
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.typedGet
import com.jeroenvdg.scrumdapp.utils.typedPost
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.invitationpage
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receiveParameters
import io.ktor.server.resources.href
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.application

fun Route.acceptInvitationsRoute() {
    val inviteService = application.dependencies.resolveBlocking<InviteService>()
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()

    typedGet<Invitations.AcceptInvitations> { inviteData ->
        val token = inviteData.token

        if (token.isNullOrBlank()) return@typedGet call.respondRedirect("/home") // type save redirect doesn't exist yet

        val invite = groupRepository.getGroupInvite(token) ?: return@typedGet call.respondRedirect("/home")
        val group = groupRepository.getGroup(invite.groupId) ?: return@typedGet call.respondRedirect("/home")

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                invitationpage(application, group, token)
            }
        }
    }

    typedPost<Invitations.AcceptInvitations> { acceptInvitationsData ->
        val token = acceptInvitationsData.token
        val password = call.receiveParameters()["group_password"]
        val userId = call.userSession.userId

        if (token.isNullOrBlank() || password.isNullOrBlank()) return@typedPost call.respondRedirect("/home")
        val invite = groupRepository.getGroupInvite(token) ?: return@typedPost call.respondRedirect("/home")

        val success = inviteService.checkGroupTokenAccess(userId, invite, password)
        if (success) {
            call.respondRedirect(application.href(GroupsRouter.Id(groupId = invite.groupId)))
        } else {

            call.respondRedirect(application.href(Invitations.AcceptInvitations(token = token), "password-mistake"))
        }
    }
}