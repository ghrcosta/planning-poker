import application.PollingManager
import config.configureRouting
import config.setup
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.features.gzip
import io.ktor.serialization.json
import io.ktor.server.netty.EngineMain
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie

val pollingManager = PollingManager()

// See resources/application.conf
fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")  // See resources/application.conf
fun Application.module() {

    // Enable support for receiving/sending request/response body as JSON (based on "Content-Type" and "Accept" headers)
    // See: https://ktor.io/docs/serialization.html
    install(ContentNegotiation) {
        json()
    }

    // Compresses responses (when applicable)
    // See: https://ktor.io/docs/compression.html
    install(Compression) {
        gzip()
    }

    // Handle request exceptions
    // See: https://ktor.io/docs/status-pages.html
    install(StatusPages) {
        setup()
    }

    // Add session cookies to identify users
    // See: https://ktor.io/docs/sessions.html
    install(Sessions) {
        cookie<ParticipantSession>("session")
    }

    configureRouting()
}

data class ParticipantSession(val roomId: String, val participantName: String)