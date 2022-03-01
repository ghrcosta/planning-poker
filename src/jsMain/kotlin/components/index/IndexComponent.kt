package components.index

import HOST
import LocalData
import Room
import csstype.Position
import csstype.pct
import csstype.translate
import external.MaterialButton
import goTo
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.fetch.RequestInit
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
    val response =
        window
            .fetch("${HOST}/create", RequestInit(method = "POST"))
            .await().text().await()
    val room: Room = Json.decodeFromString(response)

    LocalData.room = room
    goTo(room.id)
}