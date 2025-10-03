package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.input
import kotlinx.html.p

fun FlowContent.invitationpage(group: Group, token: String) {
    div(classes="c-x") {
        div(classes="card mt-c") {
            h1(classes="card-title") {+"Je bent uitgenodigd voor ${group.name}" }
            p { +"Voordat je aan de slag kan met je checkin, moet je hieronder het wachtwoord invullen dat je van je scrummaster hebt gekregen."}
            div(classes="spacer-lg")
            form(action="/invitations?token=${token}", method= FormMethod.post) {
                div(classes="vertical") {
                    input(classes="input", type=InputType.password, name="group_password")
                    div(classes="spacer-lg")
                    div(classes="hacky-icon") {
                        icon(iconName="check", classes="bg-hard")
                        input(type=InputType.submit, classes="btn btn-green") { value="Accepteer uitnodiging"}
                    }
                }
            }
        }
    }

    modal(id="password-mistake") {
        h2 {+"Verkeerd wachtwoord"}
        div(classes="spacer-lg")
        p {+"Het ingevulde wachtwoord was onjuist. Probeer opnieuw of vraag je scrummaster om het juiste wachtwoord."}
        div(classes="horizontal g-md justify-end") {
            a(classes="btn", href="#") {
                icon(iconName="undo", classes="grey")
                +"Terug"
            }
        }
    }

    modal(id="password-failure") {
        h2 {+"Fout"}
        div(classes="spacer-lg")
        p {+"Er is iets misgegaan bij het inloggen of laden van de pagina. Probeer het opnieuw of vraag je scrummaster om een nieuwe link."}
        div(classes="horizontal g-md justify-end") {
            a(classes="btn", href="#") {
                icon(iconName="undo", classes="grey")
                +"Terug"
            }
        }
    }
}