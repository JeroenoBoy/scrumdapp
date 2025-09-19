package com.jeroenvdg.scrumdapp.middileware

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.response.respond
import java.lang.Thread.sleep

val Make404 = createRouteScopedPlugin("Return 404") {
    onCall { call ->
        print("plugin")
        sleep(5000)
        call.respond(HttpStatusCode.NotFound, "Fuck you")
    }
}