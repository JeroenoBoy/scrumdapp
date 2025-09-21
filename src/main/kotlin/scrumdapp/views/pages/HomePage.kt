package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.routes.UserSession
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.img
import kotlinx.html.p

fun FlowContent.homePage() {
    h1 { +"Scrumdapp" }
    div(classes="grid row-3 g-md") {
        groupWidget("Weird Ways of Learning", "Jeroen van de Geest")
        groupWidget("Bouwers Bende", "Thomas Middelbos")
        groupWidget("Capgemini", "Steven Seagull")
    }
}

fun FlowContent.groupWidget(title: String, owner: String) {
    div(classes="card") {
        h2(classes="card-title") {+title}
        p(classes="muted") {+owner}
        img(alt="card image", src="/static/backgrounds/15.png", classes="card-img")
    }
}