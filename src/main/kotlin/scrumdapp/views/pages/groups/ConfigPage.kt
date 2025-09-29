package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.UserGroup
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.p
import kotlin.random.Random

fun FlowContent.groupConfigContent(group: Group, groupUser: UserGroup) {
    val safetyId = Random.nextInt(0, 99999999)

    h2 { +"Instellingen" }

    div(classes="spacer-lg")

    form(action="/groups/${group.id}/config/change-name", method=FormMethod.post) {
        div(classes="input-group") {
            label(classes="input-label") { htmlFor="group_name"; +"Groep Naam" }
            input(classes="input", name="group_name") {
                required=true
                minLength="3"
                maxLength="36"
                value=group.name
            }
        }
        div(classes="hacky-icon") {
            icon(iconName="check", classes="blue")
            input(type=InputType.submit, classes="btn") { value="Naam Toepassen" }
        }
    }

    if (groupUser.permissions == UserPermissions.LordOfScrum) {
        div(classes="spacer-xl")

        div(classes="danger-zone vertical g-lg") {
            h2 { +"Gevaarlijke zone" }
            div(classes="horizontal space-between align-center w-full") {
                p(classes="red") { +"Verwijder Groep" }
                div(classes="flex-1")
                a(href="#delete-group-$safetyId", classes="btn btn-red") {
                    icon(iconName="delete_forever", classes="bg-hard")
                    +"Verwijder Groep"
                }
            }
        }
    }

    modal(id="pick-background") {
    }

    modal(id="delete-group-$safetyId") {
        h2 { +"Verwijder Groep" }
        p {
            +"Deze actie kan niet en "
            b(classes="red") {+"niet ongedaan gemaakt worden"}
            +" en "
            b(classes="red") {+"verwijdert de hele groep"}
            +". Weet je het zeker?"
        }
        div(classes="horizontal g-md justify-end") {
            a(classes="btn btn-green", href="#") {
                icon(iconName="undo", classes="bg-hard")
                +"Terug"
            }
            a(classes="btn btn-red", href="#delete-group-confirm-$safetyId") {
                icon(iconName="delete_forever", classes="bg-hard")
                +"Volgende Stap"
            }
        }
    }

    modal(id="delete-group-confirm-$safetyId") {
        h2 { +"Verwijder groep" }
        p {
            +"Dit is "
            b(classes="red") { +"de laatste stap" }
            +" voordat je de groep "
            b(classes="red") { +"verwijdert" }
            +"."
        }
        p {
            +"Typ de groep naam '"
            b { +group.name }
            +"' in het veld hieronder en click op verwijder om de groep te verwijderen."
        }

        form(action="/groups/${group.id}/config/delete-group", method=FormMethod.post, classes="vertical g-md") {
            div(classes="input-group") {
                label(classes="input-label") { htmlFor="group_name"; +"Groep Naam" }
                input(classes="input", name="delete_group_name") { value="" }
            }

            div(classes="horizontal g-md justify-end") {
                a(classes = "btn btn-green", href = "#") {
                    icon(iconName = "undo", classes = "bg-hard")
                    +"Stop"
                }
                div(classes="hacky-icon") {
                    icon(iconName="check", classes="bg-hard")
                    input(type=InputType.submit, classes="btn btn-red") { value="Verwijder" }
                }
            }
        }
    }

    modal(id="delete-failed") {
        h2 { +"De groep is niet verwijdert" }
        p {
            +"Je hebt de verkeerde groeps naam ingevult."
        }
        div(classes="horizontal g-md justify-end") {
            a(classes = "btn", href = "#") {
                icon(iconName = "undo", classes = "gray")
                +"Oke"
            }
        }
    }
}