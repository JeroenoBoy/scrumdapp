package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.models.Presence
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.routes.groups.Groups
import com.jeroenvdg.scrumdapp.utils.href
import com.jeroenvdg.scrumdapp.utils.isNewCheckin
import com.jeroenvdg.scrumdapp.utils.scrumdappFormat
import com.jeroenvdg.scrumdapp.utils.scrumdappUrlFormat
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
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.input
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

fun FlowContent.checkinWidget(application: Application, checkins: List<Checkin>, group: Group, date: LocalDate, perms: UserPermissions) {

    h2 { +"Check-in voor "; b { +(date.scrumdappFormat()) } }

    table(classes="checkin-table") {
        thead {
            tr {
                th(classes="text-left name-field") {+"Naam"}
                th(classes="text-left pl-md") {+"Presentie"}
                th {+"Check-in"}
                th {+"Check-up"}
                th(classes="text-right") {+"Opmerkingen"}
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
//                        if (checkin.checkinStars == null) {
//                            +"-"
//                        } else {
//                            +floor(checkin.checkinStars!! * 0.5f).toInt().toString()
//                            if (checkin.checkinStars!! % 2 == 1) {
//                                +".5"
//                            }
//                        }
                    }
                    td(classes="text-center " + checkinColorMap[checkin.checkupStars ?: 11]) {
                        stars(checkin.checkupStars)
//                        if (checkin.checkupStars == null) {
//                            +"-"
//                        } else {
//                            +floor(checkin.checkupStars!! * 0.5f).toInt().toString()
//                            if (checkin.checkupStars!! % 2 == 1) {
//                                +".5"
//                            }
//                        }
                    }
                    td(classes="horizontal justify-between align-center max-w-om") {
                        if (checkin.comment != null) {
                            div(classes="checkbox-expand px-sm") {
                                input(type=InputType.checkBox, classes="noshow")
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
        if (perms.id <= UserPermissions.CheckinManagement.id) {
            a(href=application.href(Groups.Id.Edit(group.id, date.scrumdappUrlFormat())), classes="btn") {
                icon(iconName="edit", classes="blue")
                +"Pas aan"
            }
        }
    }
}

fun FlowContent.editableCheckinWidget(application: Application, checkins: List<Checkin>, group: Group, date: LocalDate) {
    fun FlowContent.checkinSelect(name: String, selectedValue: Int?) {
        select(classes="input select-checkin w-full text-ellipse") { this.name=name
            option(classes="gray") {value=""; if (selectedValue == null) { selected = true }; +"---" }
            option(classes="red-dim") {value="0"; if (selectedValue == 0) { selected = true }; +"0"}
            option(classes="red") {value="1"; if (selectedValue == 1) { selected = true }; +"0.5"}
            option(classes="orange-dim") {value="2"; if (selectedValue == 2) { selected = true }; +"1"}
            option(classes="orange") {value="3"; if (selectedValue == 3) { selected = true }; +"1.5"}
            option(classes="yellow-dim") {value="4"; if (selectedValue == 4) { selected = true }; +"2"}
            option(classes="yellow") {value="5"; if (selectedValue == 5) { selected = true }; +"2.5"}
            option(classes="green-dim") {value="6"; if (selectedValue == 6) { selected = true }; +"3"}
            option(classes="green") {value="7"; if (selectedValue == 7) { selected = true }; +"3.5"}
            option(classes="aqua") {value="8"; if (selectedValue == 8) { selected = true }; +"4"}
            option(classes="blue") {value="9"; if (selectedValue == 9) { selected = true }; +"4.5"}
            option(classes="blue-dim") {value="10"; if (selectedValue == 10) { selected = true }; +"5"}
        }
    }

    val isNewCheckin = checkins.isNewCheckin()
    val id = Random.nextInt(999999)

    h2 { +"Checkin voor "; b { +date.scrumdappFormat() } }

    form(method=FormMethod.post, classes="vertical g-md flex-1") {
        table(classes="checkin-table") {
            thead {
                tr {
                    th(classes="text-left name-field") {+"Naam"}
                    th(classes="text-left pl-md") {+"Presentie"}
                    th {+"Check-in"}
                    th {+"Check-up"}
                    th(classes="text-right") {+"Opmerkingen"}
                }
            }
            tbody {
                for (checkin in checkins) {
                    tr {
                        td(classes="text-ellpise name-field") { +checkin.name }
                        td(classes="pl-md ") {
                            select(classes="input select-presence w-full text-ellipse") { name="presence-${checkin.userId}"
                                option(classes="gray") {value=""; if (checkin.presence == null) { selected = true } ; +"---" }
                                option(classes="green") {value="0"; if (checkin.presence == Presence.OnTime) { selected = true }; +"Op Tijd" }
                                option(classes="yellow") {value="1"; if (checkin.presence == Presence.Late) { selected = true }; +"Te Laat" }
                                option(classes="green-dim") {value="2"; if (checkin.presence == Presence.VerifiedAbsent) { selected = true } ; +"Goorloofd Afwezig" }
                                option(classes="red") {value="3"; if (checkin.presence == Presence.Absent) { selected = true }; +"Ongeoorloofd Afwezig" }
                                option(classes="blue") {value="4"; if (checkin.presence == Presence.Sick) { selected = true }; +"Ziek" }
                            }
                        }
                        td {
                            checkinSelect("checkin-"+checkin.userId, checkin.checkinStars)
                        }
                        td {
                            checkinSelect("checkup-"+checkin.userId, checkin.checkupStars)
                        }
                        td(classes="horizontal justify-between align-center max-w-om relative") {
                            div(classes="checkbox-expand px-sm absolute") {
                                textArea(rows="5", classes="input checkbox-expand-content no-resize") {
                                    name="comment-"+checkin.userId
                                    placeholder="Opmerking..."
                                    if (checkin.comment != null) {
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
        div(classes="horizontal g-md items-center") {
            div(classes="flex-1")
            a(href="#confirm-cancel-$id", classes="btn") {
                icon(iconName="cancel", classes="gray")
                +"Annuleren"
            }
            div(classes="hacky-icon") {
                if (!isNewCheckin) {
                    icon(iconName="check", classes="blue")
                    input(type = InputType.submit, classes = "btn") { value = "Toepassen" }
                } else {
                    icon(iconName="add", classes="blue")
                    input(type = InputType.submit, classes = "btn") { value = "Maak checkin" }
                }
            }
            if (!isNewCheckin) {
                a(href="#confirm-delete-$id", classes="btn btn-red") {
                    icon(iconName="delete_forever", classes="bg-hard")
                    +"Delete"
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
                a(href=application.href(Groups.Id(groupId=group.id, date=date.scrumdappUrlFormat())), classes="btn btn-red") {
                    icon(iconName="cancel", classes="bg-hard")
                    +"Annuleren"
                }
            }
        }
    }

    if (!isNewCheckin) {
        modal(id="confirm-delete-$id") {
            form(action="/TODO", method = FormMethod.post, classes = "vertical g-md") {
                h2(classes = "modal-title") { +"Checkin verwijderen" }
                h3(classes="red") { +"TODO" }
                p { +"Weet je zeker dat je de checkin wilt verwijderen?" }
                div(classes = "horizontal g-md justify-end") {
                    a(href = "#", classes = "btn") {
                        icon(iconName = "undo", classes = "gray")
                        +"Nee"
                    }
                    div(classes = "hacky-icon") {
                        icon(iconName = "delete_forever", classes = "bg-hard")
                        input(type = InputType.submit, classes = "btn btn-red") { value = "Verwijderen" }
                    }
                }
            }
        }
    }
}
