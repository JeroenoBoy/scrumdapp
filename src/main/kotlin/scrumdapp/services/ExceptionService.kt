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

class TooManyRequestsException(
    override val message: String = "Je stuurt te veel verzoeken, even geduld",
    override val code: Int = 429,
    override val title: String = "Te veel verzoeken",
): AppException(code, message, title, log=false)

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
            if (throwable !is AppException) {
                this@configureExceptionService.log.error("Unkown exception", throwable)
            } else if (throwable.code != 400 && throwable.code != 401 && throwable.code != 403 && throwable.code != 404) {
                this@configureExceptionService.log.error("Error found in request", throwable)
            }

            val content = exceptionFromThrowable(throwable)
            call.respondHtml {
                mainLayout(this@configureExceptionService, PageData(content.title ?: "Fout")) {
                    errorPage(content)
                }
            }
        }

        status(
            HttpStatusCode.NotFound,
            HttpStatusCode.InternalServerError,
            HttpStatusCode.BadRequest,
            HttpStatusCode.TooManyRequests,
        ) {call, statusCode ->
            when(statusCode) {
                HttpStatusCode.NotFound -> {
                    throw NotFoundException()
                }
                HttpStatusCode.BadRequest -> {
                    throw ServerFaultException()
                }
                HttpStatusCode.TooManyRequests -> {
                    throw TooManyRequestsException()
                }
                HttpStatusCode.InternalServerError -> {
                    throw ServerFaultException()
                }
            }
        }
    }
}