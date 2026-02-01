package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.services.ExceptionContent
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.img
import kotlinx.html.p

fun FlowContent.errorPage(exception: ExceptionContent) {
    div(classes="c-x") {
        div(classes="card mt-c min-w-20 text-center") {
            img(src="https://http.cat/${exception.code}", alt="http-cat-${exception.code}", classes="max-w-lg card-img")
            h2(classes="card-title") {+(exception.title?:"Oops! Er is misgegaan")}
            p { +exception.message }
            div(classes="spacer-lg")
            a(href="/", classes="btn btn-blue horizontal g-lg align-center justify-center") {+"Ga terug"}
        }
    }
}

