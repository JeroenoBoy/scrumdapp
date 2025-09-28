package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.table
import kotlinx.html.thead
import kotlinx.html.tbody
import kotlinx.html.tr
import kotlinx.html.th
import kotlinx.html.td
import kotlinx.html.span
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.hr
import kotlinx.html.i
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.p
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val checkinColorMap = listOf("red", "orange-dim", "orange", "yellow-dim", "yellow", "green-dim", "green", "aqua", "blue", "blue-dim", "gray")

inline fun FlowContent.groupPage(checkins: List<Checkin>, group: Group, perm: UserPermissions, crossinline block: DIV.() -> Unit = {}) {
    h1 {+group.name}
    div(classes="horizontal g-lg") {
        div(classes="vertical g-lg") {
            div(classes="card vertical g-md") {
                i(classes="px-lg-text-center") {+"pagina's"}
                hr {}
                a(href="/groups/${group.id}/trends", classes="btn b-none px-lg text-center") {+"Trends"}
                when {
                    perm.id <= UserPermissions.ScrumDad.id -> {
                        a(href="/groups/${group.id}/users", classes="btn b-none px-lg text-center") {+"Gebruikers"}
                        a(href="/groups/${group.id}/config", classes="btn b-none px-lg text-center") {+"Instellingen"}
                    }
                    perm.id <= UserPermissions.UserManagement.id -> {
                        a(href="/groups/${group.id}/users", classes="btn b-none px-lg text-center") {+"Gebruikers"}
                    }
                }
            }
            checkinDates(checkins.map {it.date.toString()}, perm)
        }
        div(classes="card flex-1 vertical g-md") {
            block()
        }
    }
}

fun FlowContent.checkinDates(dates: List<String>, perm: UserPermissions) {
    div(classes="card vertical g-md") {
        if (perm.id <= UserPermissions.CheckinManagement.id) {
            form(classes="horizontal justify-between items-center") {
                i(classes="px-lg my-auto") {+"Data" }
                input(classes="btn btn-red", type=InputType.submit) {value="+"}
            }
        }
        hr {}
        for (date in dates) {
            a(classes = "btn b-none px-lg text-center") {+date}
        }
    }
}

fun FlowContent.checkinWidget(checkins: List<Checkin>, group: Group, date: String) {
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
                    td(classes="pl-md " + checkin.presence.color) { +checkin.presence.key }
                    td(classes="text-center " + checkinColorMap[checkin.checkinStars ?: 11]) {
                        +(checkin.checkinStars?.toString() ?: "-")
                    }
                    td(classes="text-center " + checkinColorMap[checkin.checkupStars ?: 11]) {
                        +(checkin.checkupStars?.toString() ?: "-")
                    }
                    td(classes="horizontal justify-between align-center max-w-om") {
                        if (checkin.comment != null) {
                            div(classes="checkbox-expand px-sm") {
                                input(type=InputType.checkBox, classes="noshow")
                                span(classes="text-ellipse checkbox-expand-content") {
                                    +checkin.comment
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
        a(href="/groups/${group.id}/edit?date=${date}", classes="btn") {
            icon(iconName="edit", classes="blue")
            +"Pas aan"
        }
    }
}

fun FlowContent.editableCheckinWidget(checkins: List<Checkin>, group: Group, date: String) {
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
                        td(classes="pl-md " + checkin.presence.color) { +checkin.presence.key }
                        td(classes="text-center " + checkinColorMap[checkin.checkinStars ?: 11]) {
                            +(checkin.checkinStars?.toString() ?: "-")
                        }
                        td(classes="text-center " + checkinColorMap[checkin.checkupStars ?: 11]) {
                            +(checkin.checkupStars?.toString() ?: "-")
                        }
                        td(classes="horizontal justify-between align-center max-w-om") {
                            if (checkin.comment != null) {
                                div(classes="checkbox-expand px-sm") {
                                    input(type=InputType.checkBox, classes="noshow")
                                    span(classes="text-ellipse checkbox-expand-content") {
                                        +checkin.comment
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
            a(href="#confirm-cancel", classes="btn") {
                icon(iconName="cancel", classes="gray")
                +"Annuleren"
            }
            div(classes="hacky-icon") {
                icon(iconName="check", classes="blue")
                input(type=InputType.submit, classes="btn") { value="Toepassen" }
            }
        }
    }

    modal(id="confirm-cancel") {
        div(classes="vertical g-md") {
            h2(classes="modal-title") { +"Weet je het zeker?" }
            p { +"Weet je zeker dat je de aanpassingen wil annuleren?" }
            div(classes="horizontal g-md justify-end") {
                a(href="#", classes="btn") {
                    icon(iconName="undo", classes="gray")
                    +"Terug"
                }
                a(href="/groups/${group.id}?date=${date}", classes="btn btn-red") {
                    icon(iconName="cancel", classes="bg-hard")
                    +"Annuleren"
                }
            }
        }
    }
}