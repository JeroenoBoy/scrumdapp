package scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.views.components.card
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import com.jeroenvdg.scrumdapp.views.components.renderMarkdown
import io.ktor.server.application.Application
import io.ktor.server.resources.href
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
import kotlinx.html.style
import kotlinx.html.textArea
import kotlin.random.Random

fun FlowContent.notesGroupPageContent(application: Application, groupId: Int, notes: String?, permissions: UserPermissions) {
    card {
        h2 { +"Groep Notities" }
        if (notes != null) {
            renderMarkdown(notes)
//            for (string in notes.split("\n")) {
//                p {style="white-space:pre"; +string }
//            }
        } else {
            +"Geen notities gemaakt"
        }
        if (permissions.id <= UserPermissions.CheckinManagement.id) {
            div(classes="horizontal justify-end") {
                a(href=application.href(GroupsRouter.Group.Notes.Edit(groupId)), classes="btn") {
                    icon(iconName="edit", classes="blue")
                    +"Pas aan"
                }
            }
        }
    }
}

fun FlowContent.notesGroupPageEditContent(application: Application, groupId: Int, notes: String?, permissions: UserPermissions) {
    val id = Random.nextInt(999999)

    card {
        h2 { +"Groep Notities" }
        form(method=FormMethod.post, action=application.href(GroupsRouter.Group.Notes.Edit(groupId)), classes="vertical g-md") {
            textArea(classes="input", rows="32") { name="notes"; style="resize:none"
                +(notes ?: "")
            }
            div(classes="horizontal g-md justify-end") {
                a(href="#confirm-cancel-$id", classes="btn") {
                    icon(iconName = "cancel", classes="gray")
                    +"Annuleer"
                }
                div(classes="hacky-icon") {
                    icon(iconName = "check", classes="blue")
                    input(type = InputType.submit, classes="btn") { value = "Toepassen" }
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
                a(href=application.href(GroupsRouter.Group.Notes(groupId)), classes="btn btn-red") {
                    icon(iconName="cancel", classes="bg-hard")
                    +"Annuleren"
                }
            }
        }
    }
}
