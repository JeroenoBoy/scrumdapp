package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.db.UserGroup
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.input
import kotlinx.html.option
import kotlinx.html.select
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.html.p

fun FlowContent.userEditContent(ownUserId: Int, group: Group, users: List<User>, userGroups: List<UserGroup>) {
    val token = 111

    form(action="/groups/${group.id}/users/alter-users", method=FormMethod.post, classes="vertical g-md flex-1") {
        table(classes="checkin-table") {
            thead {
                tr {
                    th(classes="text-left name-field") {+"Naam"}
                    th(classes="text-left pl-md") {+"Rol"}
                }
            }
            tbody {
                for (user in users) {
                    val userId = user.id.toString()
                    val userPermission = userGroups.find { it.userId == user.id }?.permissions ?: UserPermissions.User
                    tr {
                        td(classes="text-ellipse name-field") { +user.name }
                        if (user.id == ownUserId) {
                            td(classes="text-ellipse name-field-bl") {+userPermission.displayName}
                        } else {
                            td(classes="pl-md") {
                                select(classes = "input select-role w-full text-ellipse") {
                                    name = "role-${user.id}"
                                    option(classes = "red",) {
                                        value = "-2";
                                        if (userPermission.id >= -2) attributes["disabled"] = ""
                                        if (userPermission.id == -2) attributes["selected"] = ""
                                        +UserPermissions.LordOfScrum.displayName
                                    }
                                    option(classes = "yellow") {
                                        value = "-1";
                                        if (userPermission.id >= -1) attributes["disabled"] = ""
                                        if (userPermission.id == -1) attributes["selected"] = ""
                                        +UserPermissions.ScrumDad.displayName
                                    }
                                    option(classes = "blue") {
                                        value = "0";
                                        if (userPermission.id >= 0) attributes["disabled"] = ""
                                        if (userPermission.id == 0) attributes["selected"] = ""
                                        +UserPermissions.UserManagement.displayName
                                    }
                                    option(classes = "purple") {
                                        value = "1"
                                        if (userPermission.id == 1) attributes["selected"] = ""
                                        +UserPermissions.CheckinManagement.displayName
                                    }
                                    option(classes = "aqua") {
                                        value = "69";
                                        if (userPermission.id == 69) attributes["selected"] = ""
                                        +UserPermissions.User.displayName
                                    }
                                }
                            }
                        }

                        if (user.id != ownUserId) {
                            td(classes="pl-md") {
                                div(classes="horizontal space-between align-center") {
                                    a(classes="btn btn-red", href="#delete-user-${userId}") {
                                        icon(iconName="delete_forever", classes="bg-hard")
                                        +"Verwijder gebruiker"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        div(classes="flex-1") {

        }
        div(classes="horizontal g-md justify-end") {
            div(classes="hacky-icon") {
                icon(iconName="check", classes="blue")
                input(type=InputType.submit, classes="btn") { value="Toepassen"}
            }
            a(classes="btn", href="#create-invite-$token") {
                icon(iconName="add", classes="green")
                +"Maak uitnodiging"
            }
        }
    }

    modal(id="create-invite-${token}") {
        h2 { +"Groepsuitnodiging"}

        p { +"Kopieer, en stuur de volgende link naar je groepsgenoot."}
        br{}

        p { +"localhost:3000/invites/${token}"}

        div(classes="horizontal g-md justify-end") {
            a(classes="btn btn-green", href="#") {
                icon(iconName="check", classes="bg-hard")
                +"Terug"
            }
        }


    }

    // This is disgusting and I know it
    for (user in users) {
        modal(id="delete-user-${user.id}") {
            h2 { +"Verwijder gebruiker"}
            p {
                +"Wanneer je een gebruiker verwijderd worden ook"
                b(classes="red") {+" alle"}
                +" checkins van die gebruiker verwijderd!"
            }


            form(action="/groups/${group.id}/users/delete-user?id=${user.id}", method= FormMethod.post) {
                div(classes = "horizontal g-md justify-end") {
                    a(classes="btn btn-green", href="#") {
                        icon(iconName="undo", classes="bg-hard")
                        +"Terug"
                    }
                    div(classes="hacky-icon") {
                        icon(iconName="check", classes="bg-hard")
                        input(type=InputType.submit, classes="btn btn-red") {value="Verwijder"}
                    }
                }
            }
        }
    }


    modal(id="alter-success") {
        h2{ +"Gebruikers zijn met succes bijgewerkt!"}
        div(classes="horizontal g-md justify-end") {
            a(classes="btn", href="#") {
                icon(iconName="check", classes="grey")
                +"Terug"
            }
        }
    }

    modal(id="alter-failed") {
        h2 { +"Gebruikers zijn niet aangepast"}
        p { +"Je hebt niet de permissie of toegang om deze aanpassingen te maken"}
        div(classes="horizontal g-md justify-end") {
            a(classes="btn", href="#") {
                icon(iconName="undo", classes="grey")
                +"Terug"
            }
        }
    }
}