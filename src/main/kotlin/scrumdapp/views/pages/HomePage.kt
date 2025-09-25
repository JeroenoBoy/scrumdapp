package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.views.components.icon
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.img
import kotlinx.html.a
import kotlinx.html.span

fun FlowContent.homePage(groups: List<Group>) {
    div(classes="horizontal w-full justify-between align-center") {
        h1 { +"Scrumdapp" }
        div(classes="vertical justify-center h-full") {
            a(classes="btn btn-red horizontal") {
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
}

fun FlowContent.groupWidget(group: Group) {
    a(classes="card btn-card") {
        h2(classes="card-title") {+group.name}
//        p(classes="muted") {+group.owner.name}
        img(alt="card image", src="/static/backgrounds/15.png", classes="card-img")
    }
}