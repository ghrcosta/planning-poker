import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText

// https://developer.mozilla.org/en-US/docs/Web/HTTP/Status#client_error_responses
fun StatusPages.Configuration.setup() {
    exception<IllegalArgumentException> { cause ->
        call.respondText(
                status = HttpStatusCode.BadRequest,
                text = cause.toString()
        )
    }

    exception<NoSuchElementException> { cause ->
        call.respondText(
                status = HttpStatusCode.NotFound,
                text = cause.toString()
        )
    }

    exception<Throwable> { cause ->
        call.respondText(
                status = HttpStatusCode.InternalServerError,
                text = cause.stackTraceToString()
        )
    }
}