package components.room

import HOST
import LocalData
import Room
import csstype.Position
import csstype.pct
import csstype.translate
import external.MaterialButton
import external.MaterialTextField
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLInputElement
import org.w3c.fetch.RequestInit
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.div

val AddParticipantComponent = FC<Props> {
    div {
        css {
            position = Position.absolute
            top = 50.pct
            left = 50.pct
            transform = translate(tx = (-50).pct, ty = (-50).pct)
        }

        MaterialTextField {
            id = "userNameTextField"
            label = "Your name"
            variant = "outlined"
            required = true
        }

        MaterialButton {
            variant = "contained"
            size = "small"
            disableElevation = true

            +"Enter room"

            onClick = {
                MainScope().launch {
                    val thisElement = document.getElementById("userNameTextField") as HTMLInputElement
                    val nameTyped = thisElement.value
                    console.log("null=${nameTyped.isNotBlank()}, len=${nameTyped.length >= 3}, regex=${"[\\w| ]+".toRegex().matches(nameTyped)}")
                    if (nameTyped.isNotBlank() && nameTyped.length >= 3 && "[\\w| ]+".toRegex().matches(nameTyped)) {
                        addParticipant(nameTyped)
                    } else {
                        thisElement.value = ""
                    }
                }
            }
        }
    }
}

private suspend fun addParticipant(name: String) = coroutineScope {
    val roomId = window.location.pathname.split("/")[1]
    val response =
        window
            .fetch("${HOST}/${roomId}/addParticipant?name=${name}", RequestInit(method = "PUT"))
            .await().text().await()
    val room: Room = Json.decodeFromString(response)
    LocalData.room = room
    LocalData.userName = name
    console.log(LocalData.room)
}