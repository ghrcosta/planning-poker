import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.gzip
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.netty.EngineMain
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.script
import kotlinx.html.title

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            +"Hello from Ktor"
        }
        div {
            id = "root"
        }
        script(src = "/static/planning-poker.js") {}
    }
}

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

    routing {
        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }
        static("/static") {
            resources()
        }
    }
}