package com.jeroenvdg.scrumdapp.views

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.img
import kotlinx.html.p
import kotlinx.html.strong


fun FlowContent.loginPage() {
    div(classes = "c-x") {
        div(classes = "card mt-c") {
            h1(classes = "card-title text-center") { +"Scrumdapp" }
            p { +"Super geweldige check-in manager app" }
            p { +"Alleen voor "; strong { +"Open-ICT" }; +" studenten" }
            div(classes = "spacer-lg") {}
            a(href="/auth/login", classes="btn btn-blue horizontal g-lg align-center justify-center") {
                img(src="/static/icons/discord.svg", alt="Discord Logo", classes="icon")
                +"Log-in met Discord"
            }
        }
    }
}