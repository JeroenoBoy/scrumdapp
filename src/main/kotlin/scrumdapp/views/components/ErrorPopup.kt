package com.jeroenvdg.scrumdapp.views.components

import com.jeroenvdg.scrumdapp.services.ExceptionContent
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.p

fun FlowContent.errorPopup(exceptionContent: ExceptionContent) {
    div(classes="modal") {
        div("modal-bg")
        div(classes="modal-content card") {
            h1(classes="text-4xl text-center") {+exceptionContent.code.toString()}
            h2(classes="text-center") {+(exceptionContent.title?:"Er is misgegaan")}
            p(classes="text-center") {+exceptionContent.message}
            div(classes="horizontal g-md justify-end") {
                a(classes="btn", href="/home") {
                    icon(iconName="undo", classes="grey")
                    +"Terug"
                }
            }
        }
    }
}