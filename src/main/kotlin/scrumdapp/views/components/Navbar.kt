package com.jeroenvdg.scrumdapp.views.components

import com.jeroenvdg.scrumdapp.db.User
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.header
import kotlinx.html.i
import kotlinx.html.img
import kotlinx.html.span

fun FlowContent.navbar(user: User) {
    header(classes="nav") {
        div(classes="nav-group") {
//            a(href="/home", classes="nav-button r-full px-sm") { icon("arrow_back") }
//            span(classes="breadcrumbs-separator") {+"/"}
            i(classes="px-md") { +"Scrumdapp" }
        }
        div(classes="nav-group justify-center") {
            a(href="/home", classes="nav-button") { +"Home" }
            a(href="/dagboek", classes="nav-button") { +"Dagboek" }
            a(href="/about", classes="nav-button") { +"About" }
        }
        div(classes="nav-group justify-end") {
            div(classes="dropdown horizontal align-center g-md") {
                +user.name
                if (!user.profileImage.isEmpty()) {
                    img(alt="User Profile Picture", classes="nav-user-icon", src=user.profileImage)
                }
                div(classes="dropdown-content") {
                    a(href="/settings", classes="nav-button") {
                        icon(iconName="settings", classes="purple")
                        +"Settings"
                    }
                    a(href="/logout", classes="nav-button") {
                        icon(iconName="logout", classes="red")
                        +"Logout"
                    }
                }
            }
        }
    }
}