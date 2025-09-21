package com.jeroenvdg.scrumdapp.middleware

import com.jeroenvdg.scrumdapp.routes.UserSession
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.request.uri
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.util.url
import kotlinx.serialization.Serializable

@Serializable
data class RedirectCookie(val to: String)

val IsLoggedIn = createRouteScopedPlugin("Is Logged In") {
    onCall { call ->
        if (call.sessions.get<UserSession>() == null) {
            call.sessions.set(RedirectCookie(call.request.uri))
            call.respondRedirect("/login")
        } else if (call.sessions.get<RedirectCookie>() != null) {
            call.sessions.clear<RedirectCookie>()
        }
    }
}

val IsLoggedOut = createRouteScopedPlugin("Is Logged Out") {
    onCall { call ->
        if (call.sessions.get<UserSession>() != null) {
            call.respondRedirect("/home")
        }
    }
}