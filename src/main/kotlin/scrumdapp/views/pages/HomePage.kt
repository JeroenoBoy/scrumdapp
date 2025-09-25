package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.img
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.dialog
import kotlinx.html.form
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.span

fun FlowContent.homePage(groups: List<Group>) {
    div(classes="horizontal w-full justify-between align-center") {
        h1 { +"Scrumdapp" }
        div(classes="vertical justify-center h-full") {
            a(href="#new-group", classes="btn btn-red horizontal") {
                span(classes="my-auto") { +"Nieuwe Groep" }
                icon("add")
            }
        }
    }

    div(classes="grid row-3 g-md") {
        for (group in groups) {
            groupWidget(group)
        }
    }

    createGroupModal()
}

fun FlowContent.groupWidget(group: Group) {
    a(classes="card btn-card") {
        h2(classes="card-title") {+group.name}
//        p(classes="muted") {+group.owner.name}
        img(alt="card image", src="/static/backgrounds/15.png", classes="card-img")
    }
}

fun FlowContent.createGroupModal() {
    modal(id="new-group") {
        form(action="/groups", method=FormMethod.post, classes="vertical g-md") {
            h2(classes="modal-title") { +"Nieuwe Groep" }
            div(classes="input-group") {
                label(classes="input-label") { htmlFor="group_name"; +"Groep Naam" }
                input(classes="input", name="group_name")
            }
            div(classes="horizontal g-md justify-end") {
                a(classes="btn btn-gray", href="#") {+"Terug"}
                input(classes="btn btn-red", type=InputType.submit) { value="Maak Groep Aan" }
            }
        }
    }
}