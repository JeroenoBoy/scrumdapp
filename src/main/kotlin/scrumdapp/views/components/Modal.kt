package com.jeroenvdg.scrumdapp.views.components

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.a
import kotlinx.html.dialog
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.style

inline fun FlowContent.modal(id: String, crossinline block: DIV.() -> Unit = {}) {
    dialog(classes="modal vertical") { this.id = id
        div("modal-bg")
        div(classes="vertical h-full") {
            div { style = "flex-shrink:2;height:100%" }
            div { style = "flex-shrink:1;height:100%" }
            div(classes="modal-content card") {
                block()
            }
            div { style = "flex-shrink:1;height:100%" }
            div { style = "flex-shrink:1;height:100%" }
        }
    }
}