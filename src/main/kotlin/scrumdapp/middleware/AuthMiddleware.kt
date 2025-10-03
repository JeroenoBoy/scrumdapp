package com.jeroenvdg.scrumdapp.middleware

import com.jeroenvdg.scrumdapp.db.SessionRepository
import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.db.UserRepository
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
import io.ktor.util.AttributeKey
import kotlinx.serialization.Serializable

@Serializable
data class RedirectCookie(val to: String)

class UserProviderConfig() {
    lateinit var userRepository: UserRepository
    lateinit var sessionRepository: SessionRepository
}

private val userSessionAttributeKey = AttributeKey<UserSession>("User Session")
private val userAttributeKey = AttributeKey<User>("User")

/**
 *  Should be **checked by middleware** if it exists
 */
val ApplicationCall.user: User
    get() = attributes[userAttributeKey]

/**
 * Can check if user exists
 */
val ApplicationCall.hasUser: Boolean
    get() = attributes.getOrNull(userAttributeKey) != null

/**
 * Should be **checked by middleware** if it exists
  */
val ApplicationCall.userSession: UserSession
    get() = attributes[userSessionAttributeKey]

val UserProvider = createRouteScopedPlugin("User Provider", ::UserProviderConfig) {
    val userService = pluginConfig.userRepository
    val sessionService = pluginConfig.sessionRepository
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