package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.GroupUser
import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.middleware.ComparePermissions
import com.jeroenvdg.scrumdapp.models.Presence
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.utils.isNewCheckin
import com.jeroenvdg.scrumdapp.utils.scrumdappFormat
import com.jeroenvdg.scrumdapp.utils.scrumdappUrlFormat
import com.jeroenvdg.scrumdapp.views.components.card
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import com.jeroenvdg.scrumdapp.views.components.stars
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import kotlinx.datetime.LocalDate
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.br
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.textArea
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlin.random.Random


val checkinColorMap = listOf("red-dim", "red", "orange-dim", "orange", "yellow-dim", "yellow", "green-dim", "green", "aqua", "blue", "blue-dim", "gray")

fun FlowContent.checkinWidget(application: Application, groupUser: GroupUser, checkins: List<Checkin>, group: Group, date: LocalDate) {
    card {
        h2 { +"Check-in voor "; b { +(date.scrumdappFormat()) } }

        table(classes="checkin-table") {
            thead {
                tr {
                    th(classes="text-left name-field") { +"Naam" }
                    th(classes="text-left pl-md") { +"Presentie" }
                    th { +"Check-in" }
                    th { +"Check-up" }
                    th(classes="text-right") { +"Opmerkingen" }
                }
            }
            tbody {
                for (checkin in checkins) {
                    tr {
                        td(classes="text-ellpise name-field") { +checkin.name }
                        if (checkin.presence == null) {
                            td(classes="pl-md gray") { +"---" }
                        } else {
                            td(classes="pl-md " + checkin.presence!!.color) { +checkin.presence!!.key }
                        }
                        td(classes="text-center " + checkinColorMap[checkin.checkinStars ?: 11]) {
                            stars(checkin.checkinStars)
                        }
                        td(classes="text-center " + checkinColorMap[checkin.checkupStars ?: 11]) {
                            stars(checkin.checkupStars)
                        }
                        td(classes="horizontal justify-between align-center max-w-om") {
                            if (checkin.comment != null) {
                                div(classes="checkbox-expand px-sm") {
                                    input(type = InputType.checkBox, classes="noshow")
                                    span(classes="text-ellipse checkbox-expand-content") {
                                        +checkin.comment!!
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        div(classes="flex-1")
        div(classes="horizontal g-md justify-end") {
            if (ComparePermissions(groupUser.permissions, UserPermissions.CheckinManagement)) {
                if (checkins.all { it.presence == null}) {
                    a(href="#edit-presence", classes="btn") {
                        icon(iconName="timer", classes="red")
                        +"Registreer aanwezigheid"
                    }
                } else {
                    a(href=application.href(GroupsRouter.Group.Edit(group.id, date.scrumdappUrlFormat())), classes="btn") {
                        icon(iconName = "edit", classes="blue")
                        +"Pas aan"
                    }
                }
            }

            a(href="#own-check-in", classes="btn") {
                icon(iconName="assignment", classes="green")
                +"Eigen Check-in"
            }
        }
    }

    if (ComparePermissions(groupUser.permissions, UserPermissions.CheckinManagement)) {
        modal(id="edit-presence") {
            h2 { +"Registreer aanwezigheid voor "; b { +(date.scrumdappFormat()) } }
            br()
            form(
                classes="vertical g-md flex-1",
                action=application.href(GroupsRouter.Group.Edit.Presence(group.id, date.scrumdappUrlFormat())),
                method=FormMethod.post
            ) {
                table(classes="checkin-table") {
                    thead {
                        tr {
                            th(classes="text-left name-field") { +"Naam" }
                            th(classes="text-left pl-md") { +"Presentie" }
                        }
                    }
                    tbody {
                        tr {
                            for (checkin in checkins) {
                                td(classes="text-ellpise name-field") { +checkin.name }
                                td(classes="pl-md ") {
                                    presenceSelect(checkin.userId, checkin.presence)
                                }
                            }
                        }
                    }
                }
                div(classes="horizontal g-md justify-end") {
                    a(href="#", classes="btn") {
                        icon(iconName="cancel", classes="gray")
                        +"Annuleren"
                    }

                    div(classes="hacky-icon") {
                        icon(iconName="timer", classes="blue")
                        input(type=InputType.submit, classes="btn") { value = "Versturen" }
                    }
                }
            }
        }
    }

    modal(id="own-check-in") {
        val checkin = checkins.first { it.userId == groupUser.user.id }
        h2 { +"Eigen check-in voor "; b { +(date.scrumdappFormat()) } }
        br()
        form(
            classes="vertical g-md flex-1",
            action=application.href(GroupsRouter.Group.Edit.User(group.id, groupUser.user.id, date.scrumdappUrlFormat())),
            method=FormMethod.post
        ) {

            div(classes="input-group") {
                label(classes="input-label horizontal align-center g-sm") {
                    htmlFor="checkin"
                    icon(iconName="arrow_circle_down", classes="green")
                    span { +"Check-in" }
                }
                checkinSelect("checkin", checkin.checkinStars)
            }

            div(classes="input-group") {
                label(classes="input-label horizontal align-center g-sm") {
                    htmlFor="checkup"
                    icon(iconName="arrow_circle_up", classes="blue")
                    span { +"Check-up" }
                }
                checkinSelect("checkup", checkin.checkupStars)
            }

            br()

            label(classes="input-label horizontal align-center g-sm") {
                htmlFor="comment"
                icon(iconName="note", classes="red")
                span { +"Opmerkingen" }
            }

            textArea(classes="input no-resize", rows="8") {
                name="comment"
                +(checkin.comment ?: "")
            }

            br()

            div(classes="horizontal g-md justify-end") {
                a(href="#own-check-in-warning", classes="btn") {
                    icon(iconName="cancel", classes="gray")
                    +"Annuleren"
                }

                div(classes="hacky-icon") {
                    icon(iconName="check", classes="blue")
                    input(type=InputType.submit, classes="btn") { value = "Toepassen" }
                }
            }
        }
    }

    modal(id="own-check-in-warning") {
        h2 { +"Let op!"}
        p { +"Je veranderingen bij je eigen check-in zijn nog niet opgeslagen! Klik op "
            b(classes="green") { +"ga terug "}
            +"om je check-in alsnog op te slaan."
        }
        div(classes="horizontal g-md justify-end") {
            a(href="#", classes="btn") {
                icon(iconName="cancel", classes="red")
                +"Ik weet wat ik doe"
            }

            a(href="#own-check-in", classes="btn") {
                icon(iconName="undo", classes="green")
                +"Ga terug"
            }
        }
    }
}

fun FlowContent.editableCheckinWidget(application: Application, checkins: List<Checkin>, group: Group, date: LocalDate) {

    val isNewCheckin=checkins.isNewCheckin()
    val id=Random.nextInt(999999)

    card {

        h2 { +"Checkin voor "; b { +date.scrumdappFormat() } }

        form(method = FormMethod.post, classes="vertical g-md flex-1") {
            table(classes="checkin-table") {
                thead {
                    tr {
                        th(classes="text-left name-field") { +"Naam" }
                        th(classes="text-left pl-md") { +"Presentie" }
                        th { +"Check-in" }
                        th { +"Check-up" }
                        th(classes="text-right") { +"Opmerkingen" }
                    }
                }
                tbody {
                    for (checkin in checkins) {
                        tr {
                            td(classes="text-ellpise name-field") { +checkin.name }
                            td(classes="pl-md ") {
                                presenceSelect(checkin.userId, checkin.presence)
                            }
                            td {
                                checkinSelect("checkin-" + checkin.userId, checkin.checkinStars)
                            }
                            td {
                                checkinSelect("checkup-" + checkin.userId, checkin.checkupStars)
                            }
                            td(classes="horizontal justify-between align-center max-w-om relative") {
                                commentField(checkin.userId, checkin.comment)
                            }
                        }
                    }
                }
            }

            div(classes="flex-1")
            div(classes="horizontal g-md items-center") {
                div(classes="flex-1")
                a(href = "#confirm-cancel-$id", classes="btn") {
                    icon(iconName = "cancel", classes="gray")
                    +"Annuleren"
                }
                div(classes="hacky-icon") {
                    if (!isNewCheckin) {
                        icon(iconName = "check", classes="blue")
                        input(type = InputType.submit, classes="btn") { value = "Toepassen" }
                    } else {
                        icon(iconName = "add", classes="blue")
                        input(type = InputType.submit, classes="btn") { value = "Maak checkin" }
                    }
                }
                if (!isNewCheckin) {
                    a(href = "#confirm-delete-$id", classes="btn btn-red") {
                        icon(iconName = "delete_forever", classes="bg-hard")
                        +"Delete"
                    }
                }
            }
        }
    }

    modal(id="confirm-cancel-$id") {
        div(classes="vertical g-md") {
            h2(classes="modal-title") { +"Aanpassingen annuleren" }
            p { +"Weet je zeker dat je de aanpassingen wil annuleren?" }
            div(classes="horizontal g-md justify-end") {
                a(href="#", classes="btn") {
                    icon(iconName="undo", classes="gray")
                    +"Nee"
                }
                a(href=application.href(GroupsRouter.Group(groupId=group.id, date=date.scrumdappUrlFormat())), classes="btn btn-red") {
                    icon(iconName="cancel", classes="bg-hard")
                    +"Annuleren"
                }
            }
        }
    }

    if (!isNewCheckin) {
        modal(id="confirm-delete-$id") {
            form(action="/TODO", method=FormMethod.post, classes="vertical g-md") {
                h2(classes="modal-title") { +"Checkin verwijderen" }
                h3(classes="red") { +"TODO" }
                p { +"Weet je zeker dat je de checkin wilt verwijderen?" }
                div(classes="horizontal g-md justify-end") {
                    a(href="#", classes="btn") {
                        icon(iconName="undo", classes="gray")
                        +"Nee"
                    }
                    div(classes="hacky-icon") {
                        icon(iconName="delete_forever", classes="bg-hard")
                        input(type=InputType.submit, classes="btn btn-red") { value="Verwijderen" }
                    }
                }
            }
        }
    }
}

fun FlowContent.commentField(userid: Int?, comment: String?) {
    div(classes="checkbox-expand px-sm absolute") {
        textArea(rows = "5", classes="input checkbox-expand-content no-resize") {
            name = "comment-$userid"
            placeholder = "Opmerking..."
            if (comment != null) {
                +comment
            }
        }
    }
}

fun FlowContent.presenceSelect(userId: Int?, presence: Presence?) {
    select(classes="input select-presence w-full text-ellipse") {
        name = "presence-${userId}"
        option(classes="gray") {
            value = ""; if (presence == null) {
            selected = true
        }; +"---"
        }
        option(classes="green") {
            value = "0"; if (presence == Presence.OnTime) {
            selected = true
        }; +"Op Tijd"
        }
        option(classes="yellow") {
            value = "1"; if (presence == Presence.Late) {
            selected = true
        }; +"Te Laat"
        }
        option(classes="green-dim") {
            value = "2"; if (presence == Presence.VerifiedAbsent) {
            selected = true
        }; +"Goorloofd Afwezig"
        }
        option(classes="red") {
            value = "3"; if (presence == Presence.Absent) {
            selected = true
        }; +"Ongeoorloofd Afwezig"
        }
        option(classes="blue") {
            value = "4"; if (presence == Presence.Sick) {
            selected = true
        }; +"Ziek"
        }
    }
}

fun FlowContent.checkinSelect(name: String, selectedValue: Int?) {
    select(classes="input select-checkin w-full text-ellipse") { this.name=name
        option(classes="gray") {value=""; if (selectedValue == null) { selected=true }; +"---" }
        option(classes="red-dim") {value="0"; if (selectedValue == 0) { selected=true }; +"0"}
        option(classes="red") {value="1"; if (selectedValue == 1) { selected=true }; +"0.5"}
        option(classes="orange-dim") {value="2"; if (selectedValue == 2) { selected=true }; +"1"}
        option(classes="orange") {value="3"; if (selectedValue == 3) { selected=true }; +"1.5"}
        option(classes="yellow-dim") {value="4"; if (selectedValue == 4) { selected=true }; +"2"}
        option(classes="yellow") {value="5"; if (selectedValue == 5) { selected=true }; +"2.5"}
        option(classes="green-dim") {value="6"; if (selectedValue == 6) { selected=true }; +"3"}
        option(classes="green") {value="7"; if (selectedValue == 7) { selected=true }; +"3.5"}
        option(classes="aqua") {value="8"; if (selectedValue == 8) { selected=true }; +"4"}
        option(classes="blue") {value="9"; if (selectedValue == 9) { selected=true }; +"4.5"}
        option(classes="blue-dim") {value="10"; if (selectedValue == 10) { selected=true }; +"5"}
    }
}
