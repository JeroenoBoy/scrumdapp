package com.jeroenvdg.scrumdapp.views

import kotlinx.html.BODY
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.styleLink
import kotlinx.html.title

data class PageData(val title: String);

fun HTML.mainLayout(pageData: PageData, builder: BODY.() -> Unit = {}) {
    head {
        title("${pageData.title} | Scrumdapp")
        link("https://fonts.googleapis.com", rel = "preconnect")
        link("https://fonts.gstatic.com", rel = "preconnect")
        styleLink("https://fonts.googleapis.com/css2?family=Libertinus+Mono&family=Libertinus+Serif:ital,wght@0,400;0,600;0,700;1,400;1,600;1,700&display=swap")
        styleLink("/static/theme.css")
        styleLink("/static/styles.css")
        styleLink("https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0&icon_names=search")
    }
    body {
        builder()
    }
}

fun HTML.main(builder: BODY.() -> Unit = {}) {

}