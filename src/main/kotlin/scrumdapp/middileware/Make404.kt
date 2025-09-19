package com.jeroenvdg.scrumdapp.middileware

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.response.respond
import kotlinx.coroutines.async
import java.lang.Thread.sleep

val Make404 = createApplicationPlugin("Return 404") {
    onCall { call ->
        sleep(500)
        call.respond(HttpStatusCode.NotFound, "Fuck you")
    }
}