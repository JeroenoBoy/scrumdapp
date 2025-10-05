package com.jeroenvdg.scrumdapp.utils

import io.ktor.http.URLBuilder
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import io.ktor.server.plugins.di.DependencyKey
import io.ktor.server.plugins.di.DependencyResolver
import io.ktor.server.plugins.di.getBlocking

inline fun <reified T>DependencyResolver.resolveBlocking(): T {
    return getBlocking<T>(DependencyKey<T>())
}

inline fun <reified T: Any>Application.href(type: T, fragment: String): String {
    val builder = URLBuilder(fragment=fragment)
    this.href(type, builder)
    return builder.toString()
}