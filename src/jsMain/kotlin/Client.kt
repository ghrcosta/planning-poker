import components.index.IndexComponent
import components.room.RoomComponent
import kotlinx.browser.document
import kotlinx.browser.window
import react.create
import react.dom.render

// Yes, totally a workaround
val HOST =
    if (window.location.hostname == "localhost") {
        "http://localhost:8080"
    } else {
        "https://ghrcosta-planning-poker.appspot.com"
    }

object LocalData {
    var room: Room? = null
    var userName: String? = null
}

fun main() {
    updateNavigation()
}

fun updateNavigation() {
    val root = document.getElementById("root") ?: error("Couldn't find root container")

    // For whatever reason, adding the "react-router-dom" module to build.gradle messes up the project in IntelliJ IDEA
    // and JS code stops being recognized, so we will navigate between pages without using it.
    // And yes, there's no real error handling, frontend is not the focus of this study.
    val roomId = window.location.pathname.split("/")[1]
    val pageToRender =
        if (roomId.isBlank()) {
            IndexComponent.create()
        } else {
            RoomComponent.create()
        }
    render(pageToRender, root)
}

fun goTo(newPath: String) {
    window.history.pushState("", "", "/${newPath}")
    updateNavigation()
}