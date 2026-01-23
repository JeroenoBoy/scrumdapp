package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.routes.UserSettingsRouter
import com.jeroenvdg.scrumdapp.services.EncryptionService
import com.jeroenvdg.scrumdapp.services.EncryptionServiceImpl
import com.jeroenvdg.scrumdapp.utils.href
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.main
import kotlinx.html.p
import kotlin.random.Random

fun FlowContent.appSettingsPage(application: Application, user: User) {
    val safetyId = Random.nextInt(0, 99999999)

    h1 { +"App Instellingen" }
    div(classes="mb-lg") {
      main(classes="flex-1 vertical g-md") {
          div(classes="card vertical g-md") {
              div(classes="danger-zone vertical g-lg") {
                  h2 { +"Gevaarlijke zone" }
                  div(classes="space-between horizontal align-center w-full") {
                      p(classes="red") { +"Verwijder Scrumdapp account"}
                      div(classes="flex-1")
                      a(href="#delete-user-${safetyId}", classes="btn btn-red") {
                          icon(iconName="delete_forever", classes="bg-hard")
                          +"Verwijder Scrumdapp account"
                      }
                  }
              }
          }
      }
    }

    modal(id="delete-user-${safetyId}") {
        h2 { +"Verwijder Scrumdapp account"}
        p {
            +"Deze actie kan "
            b(classes="red") { +"niet ongedaan gemaakt worden!"}
            + " En kan er mogelijk voor zorgen dat je "
            b(classes="red") {+"niet "}
            + "je aanwezigheid kan aantonen!"
        }

        div(classes="horizontal g-md justify-end") {
            a(classes="btn btn-green", href="#") {
                icon(iconName="undo", classes="bg-hard")
                +"Terug"
            }
            a(classes="btn btn-red", href="#confirm-delete-user-${safetyId}") {
                icon(iconName="delete_forever", classes="bg-hard")
                +"Volgende Stap"
            }
        }
    }

    modal(id="confirm-delete-user-${safetyId}") {
        h2 { +"Verwijder Scrumdapp account"}
        p {
            +"Dit is het "
            b(classes="red") { +"laatste "}
            +"moment voordat je Scrumdapp account permanent wordt verwijderd. "
            +"Vul hieronder je hele naam in en klik op 'verwijder account' om je afmelding te bevestigen. "
            +"Hou er rekening mee dat de aanwezigheid geregistreerd in Scrumdapp "
            b(classes="red") { +"noodzakelijk " }
            +"kan zijn voor jouw assessment. Als je dit nog nodig hebt gebruik de exporteer functie in de 'trends' pagina."
        }
        form(action=application.href(UserSettingsRouter.Delete()), method= FormMethod.post, classes="vertical g-md") {
            div(classes="input-group") {
                label(classes="input-label") { htmlFor="user_name"; +"Jouw achternaam"}
                input(classes="input", name="delete_user_name") { value=""}
            }
            div(classes="spacer-lg")
            div(classes="horizontal justify-end g-md") {
                a(classes = "btn btn-green", href="#") {
                    icon(iconName = "undo", classes = "bg-hard")
                    +"Stop"
                }
                div(classes="hacky-icon") {
                    icon(iconName="check", classes="bg-hard")
                    input(type= InputType.submit, classes="btn btn-red") { value="Verwijder account"}
                }
            }
        }
    }

    modal(id="delete-user-failed") {
        h2 { +"Je account is niet verwijdert!"}
        p {+"Je hebt de verkeerde naam opgegeven."}
        div(classes="horizontal g-md justify-end") {
            a(classes="btn", href="#") {
                icon(iconName = "undo", classes = "gray")
                +"Oke"
            }
        }
    }
}