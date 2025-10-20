package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.views.PageData
import com.jeroenvdg.scrumdapp.views.mainLayout
import com.jeroenvdg.scrumdapp.views.pages.errorPage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.statuspages.*
import kotlinx.serialization.Serializable

@Serializable
data class ExceptionContent(
    val code: Int,
    val title: String?,
    val message: String,
    val stackTrace: String
)
class ValidationException(
    override val message: String = "Er is iets gegaan bij het verwerken van je verzoek, probeer opnieuw.",
    override val code: Int = 400,
    override val title: String = "Validatiefout",
): AppException(code, message, title, log=false)

class NotAuthorizedException(
    override val message: String,
    override val code: Int = 401,
    override val title: String = "Geen toegang ):",
): AppException(code, message, title, log=false)

class NoAccessException(
    override val message: String,
    override val code: Int = 403,
    override val title: String = "Verboden",
): AppException(code, message, title, log=false)

class NotFoundException(
    override val message: String = "De gevraagde pagina is niet gevonden of bestaat niet.",
    override val code: Int = 404,
    override val title: String = "Pagina niet gevonden"
): AppException(code, message, title, log = false)

class ServerFaultException(
    override val message: String = "Er is misgegaan, probeer het later opnieuw.",
    override val code: Int = 500,
    override val title: String = "Interne serverfout",
): AppException(code, message, title, log=false)

open class AppException(
    open val code: Int,
    override val message: String,
    open val title: String?,
    open val log: Boolean = false // Change this to enum?
): RuntimeException(message)

fun Throwable.toExceptionContent(): ExceptionContent {
    return exceptionFromThrowable(this)
}

fun exceptionFromThrowable(throwable: Throwable): ExceptionContent {
    return when (throwable) {
        is AppException -> ExceptionContent(
            code = throwable.code,
            title = throwable.title,
            message = throwable.message,
            stackTrace = throwable.stackTraceToString()
        )
        else -> ExceptionContent(
            code = 500,
            title = "Onverwachte fout",
            message = throwable.message ?: "Onbekende fout",
            stackTrace = throwable.stackTraceToString()
        )
    }
}

fun Application.configureExceptionService() {
    install(StatusPages) {
        exception<Throwable> { call, throwable ->
            val content = exceptionFromThrowable(throwable)
            call.respondHtml {
                mainLayout(PageData(content.title ?: "Fout")) {
                    errorPage(content)
                }
            }
        }

        status(
            HttpStatusCode.NotFound,
            HttpStatusCode.InternalServerError,
            HttpStatusCode.BadRequest,
        ) {call, statusCode ->
            when(statusCode) {
                HttpStatusCode.NotFound -> {
                    throw NotFoundException()
                }
                HttpStatusCode.BadRequest -> {
                    throw ServerFaultException()
                }
                HttpStatusCode.InternalServerError -> {
                    throw ServerFaultException()
                }
            }
        }
    }
}