package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.db.UserGroup
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
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.hr
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.textArea

val checkinColorMap = listOf("red", "orange-dim", "orange", "yellow-dim", "yellow", "green-dim", "green", "aqua", "blue", "blue-dim", "gray")

inline fun FlowContent.groupPage(checkins: List<Checkin>, group: Group, userPermissions: UserPermissions, crossinline block: DIV.() -> Unit = {}) {
    h1 {+group.name}
    div(classes="horizontal g-lg") {
        div(classes="vertical g-lg") {
            div(classes="card vertical g-md") {
                i(classes="px-lg text-center") {+"Pagina's"}
                hr {}
                a(href="/groups/${group.id}/trends", classes="btn b-none px-lg") {
                    icon(iconName="bar_chart", classes="yellow")
                    +"Trends"
                }
                if (userPermissions.id <= UserPermissions.UserManagement.id) {
                    a(href="/groups/${group.id}/users", classes="btn b-none px-lg") {
                        icon(iconName="groups", classes="blue")
                        +"Gebruikers"
                    }
                }
                if (userPermissions.id <= UserPermissions.ScrumDad.id) {
                    a(href="/groups/${group.id}/config", classes="btn b-none px-lg") {
                        icon(iconName="settings", classes="purple")
                        +"Instellingen"
                    }
                }
            }
            checkinDates(checkins.map {it.date.toString()}, userPermissions)
        }
        div(classes="card flex-1 vertical g-md") { id="group-content"
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

fun FlowContent.checkinWidget(checkins: List<Checkin>, group: Group, date: String, perms: UserPermissions) {
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
        if (perms.id >= UserPermissions.CheckinManagement.id) {
            a(href="/groups/${group.id}/edit?date=${date}", classes="btn") {
                icon(iconName="edit", classes="blue")
                +"Pas aan"
            }
        }
    }
}

fun FlowContent.editableCheckinWidget(checkins: List<Checkin>, group: Group, date: String) {
    fun FlowContent.checkinSelect(name: String) {
        select(classes="input select-presence w-full text-ellipse") { this.name=name
            option(classes="red-dim") {value="0"; +"0"}
            option(classes="red") {value="1"; +"0.5"}
            option(classes="orange-dim") {value="2"; +"1"}
            option(classes="orange") {value="3"; +"1.5"}
            option(classes="yellow-dim") {value="4"; +"2"}
            option(classes="yellow") {value="5"; +"2.5"}
            option(classes="green-dim") {value="6"; +"3"}
            option(classes="green") {value="7"; +"3.5"}
            option(classes="aqua") {value="8"; +"4"}
            option(classes="blue") {value="9"; +"4.5"}
            option(classes="blue-dim") {value="10"; +"5"}
        }
    }

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
                            select(classes="input select-presence w-full text-ellipse") { name="presence"
                                option(classes="gray") {+"---"}
                                option(classes="green") {value="0"; +"---"}
                                option(classes="yellow") {value="1"; +"Te Laat"}
                                option(classes="green-dim") {value="2"; +"Goorloofd Afwezig"}
                                option(classes="red") {value="3"; +"Ongeoorloofd Afwezig"}
                                option(classes="blue") {value="4"; +"Ziek"}
                            }
                        }
                        td {
                            checkinSelect("checkin-"+checkin.id)
                        }
                        td {
                            checkinSelect("checkout-"+checkin.id)
                        }
                        td(classes="horizontal justify-between align-center max-w-om relative") {
                            div(classes="checkbox-expand px-sm absolute") {
                                textArea(rows="5") {
                                    name="addition-"+checkin.id
                                    placeholder="Opmerking..."
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
                    +"Nee"
                }
                a(href="/groups/${group.id}?date=${date}", classes="btn btn-red") {
                    icon(iconName="cancel", classes="bg-hard")
                    +"Annuleren"
                }
            }
        }
    }
}
