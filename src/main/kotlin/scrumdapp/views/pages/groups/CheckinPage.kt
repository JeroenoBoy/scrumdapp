package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
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
                    if (checkin.presence == null) {
                        td(classes="pl-md gray") { +"---" }
                    } else {
                        td(classes="pl-md " + checkin.presence.color) { +checkin.presence.key }
                    }
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
        select(classes="input select-checkin w-full text-ellipse") { this.name=name
            option(classes="gray") {value=""; +"---"}
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

    val id = Random.nextInt(999999)

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
                                option(classes="gray") {value=""; +"---"}
                                option(classes="green") {value="0"; +"Op Tijd"}
                                option(classes="yellow") {value="1"; +"Te Laat"}
                                option(classes="green-dim") {value="2"; +"Goorloofd Afwezig"}
                                option(classes="red") {value="3"; +"Ongeoorloofd Afwezig"}
                                option(classes="blue") {value="4"; +"Ziek"}
                            }
                        }
                        td {
                            checkinSelect("checkin-"+checkin.userId)
                        }
                        td {
                            checkinSelect("checkout-"+checkin.userId)
                        }
                        td(classes="horizontal justify-between align-center max-w-om relative") {
                            div(classes="checkbox-expand px-sm absolute") {
                                textArea(rows="5", classes="input checkbox-expand-content no-resize") {
                                    name="addition-"+checkin.userId
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
            a(href="#confirm-cancel-$id", classes="btn") {
                icon(iconName="cancel", classes="gray")
                +"Annuleren"
            }
            div(classes="hacky-icon") {
                icon(iconName="check", classes="blue")
                input(type=InputType.submit, classes="btn") { value="Toepassen" }
            }
            a(href="#confirm-delete-$id", classes="btn btn-red") {
                icon(iconName="delete_forever", classes="bg-hard")
                +"Delete"
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
                a(href="/groups/${group.id}?date=${date}", classes="btn btn-red") {
                    icon(iconName="cancel", classes="bg-hard")
                    +"Annuleren"
                }
            }
        }
    }

    modal(id="confirm-delete-$id") {
        form(action="/groups/${group.id}/delete-checkin?date=${date}", method=FormMethod.post, classes="vertical g-md") {
            h2(classes="modal-title") { +"Checkin verwijderen" }
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
