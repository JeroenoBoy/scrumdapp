package com.jeroenvdg.scrumdapp.views.components

import kotlinx.html.FlowContent
import kotlinx.html.div

fun FlowContent.card(classes: String? = null, block: FlowContent.() -> Unit) {
    if (classes == null) {
        div(classes="card vertical g-md", block)
    } else if (classes.contains("horizontal")) {
        div(classes="card g-md $classes", block)
    } else {
        div(classes="card vertical g-md $classes", block)
    }
}