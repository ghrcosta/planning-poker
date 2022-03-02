package components.addParticipant

import HOST
import config.Network
import csstype.Position
import csstype.TextAlign
import csstype.pct
import csstype.px
import csstype.translate
import external.MaterialButton
import external.MaterialTextField
import external.encodeURIComponent
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.core.use
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import react.FC
import react.Props
import react.css.css
import react.dom.events.KeyboardEvent
import react.dom.events.MouseEvent
import react.dom.html.ReactHTML.div
import updateNavigation

val AddParticipantComponent = FC<Props> {
    div {
        css {
            position = Position.absolute
            top = 50.pct
            left = 50.pct
            transform = translate(tx = (-50).pct, ty = (-50).pct)
        }

        div {
            css {
                width = 200.px
            }
            MaterialTextField {
                id = "userNameTextField"
                label = "Your name"
                variant = "outlined"
                required = true

                onKeyUp = textAreaKeyUp
            }

            div {
                css {
                    marginTop = 8.px
                    textAlign = TextAlign.center
                }
                MaterialButton {
                    variant = "contained"
                    size = "medium"
                    disableElevation = true

                    +"Enter room"

                    onClick = nameButtonClicked
                }
            }
        }
    }
}


private val textAreaKeyUp = { event: KeyboardEvent<HTMLTextAreaElement> ->
    if (event.code == "Enter") {
        nameButtonClicked(null)
    }
}


private val nameButtonClicked = { _: MouseEvent<HTMLButtonElement, *>? ->
    MainScope().launch {
        val textField = document.getElementById("userNameTextField") as HTMLInputElement
        val nameTyped = encodeURIComponent(textField.value)
        if (nameTyped.isNotBlank() && nameTyped.length >= 3 && "[\\w|%]+".toRegex().matches(nameTyped)) {
            addParticipant(nameTyped)
        } else {
            textField.value = ""
        }
    }
    Unit
}

private suspend fun addParticipant(name: String) = coroutineScope {
    val roomId = window.location.pathname.split("/")[1]
    val url = "${HOST}/${roomId}/addParticipant?name=${name}"

    val response: HttpResponse = Network.getClient().use { it.put(url) }  // Session cookie is automatically set
    val responseText = response.readText()

    if (response.status != HttpStatusCode.NoContent) {
        Network.showRequestErrorAlert(responseText)
        return@coroutineScope
    }

    updateNavigation()
}