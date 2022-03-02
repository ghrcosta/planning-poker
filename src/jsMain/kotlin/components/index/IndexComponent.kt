package components.index

import HOST
import Room
import config.Network
import csstype.Position
import csstype.pct
import csstype.translate
import external.MaterialButton
import goTo
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.core.use
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.div

val IndexComponent = FC<Props> {
    div {
        css {
            position = Position.absolute
            top = 50.pct
            left = 50.pct
            transform = translate(tx = (-50).pct, ty = (-50).pct)
        }

        MaterialButton {
            variant = "contained"
            size = "large"
            disableElevation = true

            +"Create room"

            onClick = {
                MainScope().launch {
                    createRoom()
                }
            }
        }
    }
}

suspend fun createRoom() = coroutineScope {
    val url = "${HOST}/create"

    val response: HttpResponse = Network.getClient().use { it.post(url) }
    val responseText = response.readText()

    if (response.status != HttpStatusCode.OK) {
        Network.showRequestErrorAlert(responseText)
        return@coroutineScope
    }

    val room: Room = Json.decodeFromString(responseText)  // No need to save
    goTo(room.id)
}