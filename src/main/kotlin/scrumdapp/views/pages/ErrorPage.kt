package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.services.ExceptionContent
import io.ktor.server.application.Application
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.p

fun FlowContent.errorPage(exception: ExceptionContent) {
    div(classes="c-x") {
        div(classes="card mt-c") {
            h1(classes="card-title text-center") {+(exception.title?:"Oops! Er is misgegaan")}
            p { +exception.message }
        }
    }
}