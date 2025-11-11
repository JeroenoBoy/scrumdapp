package com.jeroenvdg.scrumdapp.views.components

import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.routes.AboutRouter
import com.jeroenvdg.scrumdapp.routes.HomeRouter
import com.jeroenvdg.scrumdapp.routes.LogoutRouter
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.header
import kotlinx.html.i
import kotlinx.html.img

fun FlowContent.navbar(application: Application, user: User) {
    header(classes="nav") {
        div(classes="nav-group") {
//            a(href="/home", classes="nav-button r-full px-sm") { icon("arrow_back") }
//            span(classes="breadcrumbs-separator") {+"/"}
            i(classes="px-lg") { +"Scrumdapp" }
        }
        div(classes="nav-group justify-center") {
            a(href=application.href(HomeRouter()), classes="nav-button") { +"Home" }
            a(href="/", classes="nav-button") { +"Dagboek" }
            a(href=application.href(AboutRouter()), classes="nav-button") { +"Over" }
        }
        div(classes="nav-group justify-end") {
            div(classes="nav-dropdown horizontal align-center g-md") {
                +user.name
                if (!user.profileImage.isEmpty()) {
                    img(alt="User Profile Picture", classes="nav-user-icon", src=user.profileImage)
                }
                div(classes="nav-dropdown-content") {
                    a(href="/settings", classes="nav-button") {
                        icon(iconName="settings", classes="purple")
                        +"Settings"
                    }
                    a(href=application.href(LogoutRouter()), classes="nav-button") {
                        icon(iconName="logout", classes="red")
                        +"Logout"
                    }
                }
            }
        }
    }
}