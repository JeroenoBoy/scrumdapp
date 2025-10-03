package com.jeroenvdg.scrumdapp.views.components

import kotlinx.html.FlowContent
import kotlinx.html.span

fun FlowContent.stars(amount: Int?, classes: String? = null) {
    if (amount == null) {
        span(classes=classes) {
            span(classes="gray") { +"-" }
        }
        return
    }

    span(classes="stars" + if(classes==null) "" else " $classes") {
        for (i in 1..5) {
            if ((i-1)*2+1 > amount) {
                icon("star_outline")
            } else if (i*2 > amount) {
                icon("star_half")
            } else {
                icon("star")
            }
        }
    }
}
