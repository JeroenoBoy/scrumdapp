package com.jeroenvdg.scrumdapp.routes.invites

import com.jeroenvdg.scrumdapp.middleware.group
import com.jeroenvdg.scrumdapp.middleware.groupUser
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.services.InviteService
import com.jeroenvdg.scrumdapp.utils.href
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.typedPost
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.groups.groupPage
import com.jeroenvdg.scrumdapp.views.pages.groups.userInviteContent
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.plugins.origin
import io.ktor.server.request.receiveParameters
import io.ktor.server.resources.href
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.application

fun Route.createInvitationsRoute() {
    val inviteService = application.dependencies.resolveBlocking<InviteService>()

    typedPost<Invitations.CreateInvitation.Id>() {
        val group = call.group
        val password = call.receiveParameters()["create_group_invite"]

        val token = inviteService.createInviteToken(password, group.id)
        if (token.isNullOrBlank()) return@typedPost call.respondRedirect(application.href(GroupsRouter.Group.Users(group.id), "create-invite"))

        val origin = call.request.origin.serverHost
        val url = "https://$origin${application.href(Invitations.AcceptInvitations(token = token))}"

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call)) {
                groupPage(application, emptyList(), group, call.groupUser.permissions) {
                    userInviteContent(application, group, url)
                }
            }
        }
    }
}