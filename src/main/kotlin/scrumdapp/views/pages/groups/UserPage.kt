package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.db.UserGroup
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.views.components.icon
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.input
import kotlinx.html.option
import kotlinx.html.select
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr

fun FlowContent.userEditContent(users: List<User>, userGroups: List<UserGroup>) {
    form(method=FormMethod.put, classes="vertical g-md flex-1") {
        table(classes="checkin-table") {
            thead {
                tr {
                    th(classes="text-left name-field") {+"Naam"}
                    th(classes="text-left pl-md") {+"Rol"}
                }
            }
            tbody {
                for (user in users) {
                    val userPermission = userGroups.find { it.userId == user.id }?.permissions ?: UserPermissions.User
                    tr {
                        td(classes="text-ellipse name-field") { +user.name }
                        td(classes="pl-md") {
                            select(classes = "input select-role w-full text-ellipse") { name="role"
                                option(classes="red",) {
                                    value="-2";
                                    if (userPermission.id > -2) attributes["disabled"] = ""
                                    if (userPermission.id == -2) attributes["selected"] = ""
                                    +"Scrumdad+"}
                                option(classes="yellow") {
                                    value="-1";
                                    if (userPermission.id > -1) attributes["disabled"] = ""
                                    if (userPermission.id == -1) attributes["selected"] = ""
                                    +"Scrumdad"}
                                option(classes="blue") {
                                    value="0";
                                    if (userPermission.id > 0) attributes["disabled"] = ""
                                    if (userPermission.id == 0) attributes["selected"] = ""
                                    +"User management"}
                                option(classes="purple") {
                                    value="1";
                                    if (userPermission.id == 1) attributes["selected"] = ""
                                    +"Checkin management"}
                                option(classes="aqua") {
                                    value="69";
                                    if (userPermission.id == 69) attributes["selected"] = ""
                                    +"Lid"}
                            }
                        }
                    }
                }
            }
        }
        div(classes="flex-1")
        div(classes="horizontal g-md justify-end") {
            div(classes = "hacky-icon") {
                icon(iconName="check", classes="blue")
                input(type=InputType.submit, classes="btn") { value="Toepassen"}
            }


        }
    }
}