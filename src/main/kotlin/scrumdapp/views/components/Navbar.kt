package com.jeroenvdg.scrumdapp.views.components

import com.jeroenvdg.scrumdapp.routes.UserData
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.header
import kotlinx.html.i
import kotlinx.html.img
import kotlinx.html.span

fun FlowContent.navbar(userData: UserData) {
    header(classes="nav") {
        div(classes="nav-group") {
            a(href="/home", classes="nav-button r-full px-sm") { icon("arrow_back") }
            span(classes="breadcrumbs-separator") {+"/"}
            i { +"Scrumdapp" }
        }
        div(classes="nav-group justify-center") {
            a(href="/home", classes="nav-button") { +"Home" }
            a(href="/about", classes="nav-button") { +"About" }
        }
        div(classes="nav-group justify-end") {
            div(classes="dropdown horizontal align-center g-md") {
                +userData.name
                img(alt="User Profile Picture", classes="nav-user-icon", src="https://cdn.discordapp.com/avatars/${userData.discordId}/${userData.avatar}.png")
                div(classes="dropdown-content") {
                    a(href="/settings", classes="nav-button") {
                        icon(iconName="settings")
                        +"Settings"
                    }
                    a(href="/logout", classes="nav-button") {
                        icon(iconName="logout")
                        +"Logout"
                    }
                }
            }
        }
    }
}