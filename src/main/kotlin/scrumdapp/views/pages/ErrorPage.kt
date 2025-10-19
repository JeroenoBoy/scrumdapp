package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.services.ExceptionContent
import com.jeroenvdg.scrumdapp.views.components.forcedModal
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import io.ktor.server.application.Application
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.p

fun FlowContent.errorPage(exception: ExceptionContent) {
    div(classes="c-x") {
        div(classes="card mt-c") {
            h1(classes="text-4xl text-center my-auto") {+exception.code.toString()}
            h2(classes="card-title") {+(exception.title?:"Oops! Er is misgegaan")}
            p { +exception.message }
            div(classes="spacer-lg")
            a(href="/", classes="btn btn-blue horizontal g-lg align-center justify-center") {+"Ga terug"}
        }
    }
}

