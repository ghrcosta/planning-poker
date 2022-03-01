package config

import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.link
import kotlinx.html.script

fun HTML.defaultPage() {
    head {
        // Roboto font, used by Material UI components
        link(rel = "stylesheet", href = "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700&display=swap")
    }

    body {
        // Base div (required)
        div {
            id = "root"
        }

        // Our client code (must be added in the body)
        script(src = "/static/planning-poker.js") {}
    }
}