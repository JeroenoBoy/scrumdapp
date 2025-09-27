package com.jeroenvdg.scrumdapp.views.pages

import kotlinx.html.FlowContent
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.ul

fun FlowContent.aboutPage() {
    h1 { +"Scrumdapp" }
    p { +"De ultieme check-in manager voor jouw project." }
    p {
        +"Scrumdapp, aka Scrum Daddy App is ontwikkeld uit frustratie om makkelijk check-ins bij te houden van je team. Wij willen hier een simpelere manier om dit bij te houden met een prachtige UI en security & privacy features by design. `(*>﹏<*)′ "
    }
    h2 {+"Ontwikkeld door"}
    ul {
        li { +"Jeroen van de Geest" }
        li { +"Daan Meijneken" }
        li { +"Luc van Ogtrop" }
    }
}