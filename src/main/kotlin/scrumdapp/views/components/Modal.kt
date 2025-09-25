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

inline fun FlowContent.modal(id: String, crossinline block: DIV.() -> Unit = {}) {
    dialog(classes="modal") { this.id = id
        div("modal-bg")
        div(classes="modal-content card") {
            block()
        }
    }
}