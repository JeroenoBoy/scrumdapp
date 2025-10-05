package com.jeroenvdg.scrumdapp.utils

import io.ktor.http.HttpMethod
import io.ktor.server.resources.Resources
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.method
import io.ktor.server.resources.handle
import io.ktor.server.resources.resource
import io.ktor.server.routing.OptionalParameterRouteSelector
import io.ktor.server.routing.ParameterRouteSelector
import io.ktor.server.routing.Routing
import io.ktor.server.routing.createRouteFromPath
import kotlinx.serialization.serializer

/**
 * Like get<T>() but meant to be nested while still being type safe
 */
inline fun <reified T: Any>Route.typedGet(noinline body: suspend RoutingContext.(T) -> Unit) {
    method(HttpMethod.Get) {
        handle<T>{
            body(it)
        }
    }
}

/**
 * Like post<T>() but meant to be nested while still being type safe
 */
inline fun <reified T: Any>Route.typedPost(noinline body: suspend RoutingContext.(T) -> Unit) {
    method(HttpMethod.Post) {
        handle<T>{
            body(it)
        }
    }
}

/**
 * Copied from io.ktor.server.resources.resource<T>
 * Basically the same function, but only grabs the last route so it can be nested.
 * Ex:
 * ```kt
 * resource<Groups> {
 *   install(IsLoggedIn)
 *   /* */
 * }
 * resource<Groups.Id> {
 *   install(IsLoggedIn)
 *   install(IsInGroup)
 *   /* */
 * }
 * // Becomes:
 * route<Groups> {
 *   install(IsLoggedIn)
 *   /* */
 *   route<Groups.Id> {
 *     install(IsInGroup)
 *   }
 * }
 * ```
 */
inline fun <reified T: Any>Route.route(noinline body: Route.() -> Unit) {
    val serializer = serializer<T>()

    val resources = plugin(Resources)
    val path = resources.resourcesFormat.encodeToPathPattern(serializer).split('/').last()
    val queryParameters = resources.resourcesFormat.encodeToQueryParameters(serializer)
    val route = createRouteFromPath(path)

    queryParameters.fold(route) { entry, query ->
        val selector = if (query.isOptional) {
            OptionalParameterRouteSelector(query.name)
        } else {
            ParameterRouteSelector(query.name)
        }
        entry.createChild(selector)
    }.apply(body)
}