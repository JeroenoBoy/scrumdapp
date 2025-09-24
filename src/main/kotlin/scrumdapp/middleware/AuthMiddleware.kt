package com.jeroenvdg.scrumdapp.middleware

import com.jeroenvdg.scrumdapp.db.SessionService
import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.db.UserService
import com.jeroenvdg.scrumdapp.models.UserSession
import com.jeroenvdg.scrumdapp.routes.SessionToken
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.request.uri
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.util.url
import io.ktor.util.AttributeKey
import kotlinx.serialization.Serializable

@Serializable
data class RedirectCookie(val to: String)

class UserProviderConfig() {
    lateinit var userService: UserService
    lateinit var sessionService: SessionService
}

private val userSessionAttributeKey = AttributeKey<UserSession>("User Session")
private val userAttributeKey = AttributeKey<User>("User")

/**
 *  Should be **checked by middleware** if it exists
 */
val ApplicationCall.user: User
    get() = attributes[userAttributeKey]

/**
 * Should be **checked by middleware** if it exists
  */
val ApplicationCall.userSession: UserSession
    get() = attributes[userSessionAttributeKey]

val UserProvider = createRouteScopedPlugin("User Provider", ::UserProviderConfig) {
    val userService = pluginConfig.userService
    val sessionService = pluginConfig.sessionService
    onCall { call ->
        val sessionToken = call.sessions.get<SessionToken>()?.token ?: return@onCall
        val session = sessionService.getSession(sessionToken) ?: return@onCall call.sessions.clear<SessionToken>()
        val user = userService.getUser(session.userId) ?: return@onCall call.sessions.clear<SessionToken>()
        call.attributes.put(userAttributeKey, user)
        call.attributes.put(userSessionAttributeKey, session)
    }
}

val IsLoggedIn = createRouteScopedPlugin("Is Logged In") {
    onCall { call ->
        if (call.attributes.getOrNull(userAttributeKey) == null) {
            call.sessions.set(RedirectCookie(call.request.uri))
            call.respondRedirect("/login")
        } else if (call.sessions.get<RedirectCookie>() != null) {
            call.sessions.clear<RedirectCookie>()
        }
    }
}

val IsLoggedOut = createRouteScopedPlugin("Is Logged Out") {
    onCall { call ->
        if (call.attributes.getOrNull(userAttributeKey) != null) {
            call.respondRedirect("/home")
        }
    }
}