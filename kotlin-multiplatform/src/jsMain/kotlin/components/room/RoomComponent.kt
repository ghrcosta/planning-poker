package components.room

import LocalData
import components.Color
import csstype.AlignSelf
import csstype.Cursor
import csstype.Display
import csstype.Flex
import csstype.FontFamily
import csstype.FontSize
import csstype.JustifyContent
import csstype.JustifyItems
import csstype.ListStyleType
import csstype.TextAlign
import csstype.px
import external.MaterialButton
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.HTMLUListElement
import org.w3c.dom.get
import org.w3c.dom.set
import react.FC
import react.Props
import react.ReactElement
import react.create
import react.css.css
import react.dom.events.MouseEvent
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.ul
import react.dom.render
import voteToFloat

// https://developer.mozilla.org/en-US/docs/Learn/HTML/Howto/Use_data_attributes
object DataAttribute {
    const val IS_MOUSE_HIGHLIGHTED = "isMouseHighlighted"
    const val IS_VOTE_HIGHLIGHTED = "isVoteHighlighted"
}

object RoomUI {
    fun create(): ReactElement {
        startSync()
        return RoomComponent.create()
    }
}

private const val revealButtonId = "revealButton"
private const val clearButtonId = "clearButton"
private const val voteListRootId = "voteListRoot"
private const val voteAverageRootId = "voteAverageRootId"
private const val voteAverageValueId = "voteAverageValueId"

private val RoomComponent = FC<Props> {
    div {
        css {
            display = Display.flex
            justifyContent = JustifyContent.center
            fontFamily = FontFamily.monospace
            marginTop = 20.px
        }
        LocalData.voteOptions.forEach { voteOption ->
            div {
                id = voteOption.key

                css {
                    display = Display.flex
                    width = 40.px
                    height = 60.px
                    margin = 6.px
                    backgroundColor = Color.GREY_PRIMARY.asBackgroundColorObj()
                    cursor = "pointer".unsafeCast<Cursor>()
                }

                span {
                    css {
                        flex = "1".unsafeCast<Flex>()
                        textAlign = TextAlign.center
                        alignSelf = AlignSelf.center
                        fontSize = FontSize.xLarge
                    }
                    +voteOption.value
                }

                onMouseOver = onMouseOverCard
                onMouseOut = onMouseOutCard
                onClick = voteCardClicked
            }
        }
    }
    div {
        css {
            display = Display.flex
            justifyContent = JustifyContent.center
            textAlign = TextAlign.center
            marginTop = 20.px
        }
        div {
            css {
                display = Display.grid
                textAlign = TextAlign.center
                width = 100.px
                marginRight = 10.px
            }
            MaterialButton {
                variant = "contained"
                size = "medium"
                disableElevation = true

                +"Reveal"

                id = revealButtonId
                onClick = revealVotesButtonClicked
            }
        }
        div {
            css {
                display = Display.grid
                textAlign = TextAlign.center
                width = 100.px
                marginLeft = 10.px
            }
            MaterialButton {
                variant = "contained"
                size = "medium"
                disableElevation = true

                +"Clear"

                id = clearButtonId
                onClick = clearVotesButtonClicked
            }
        }
    }
    div {
        css {
            marginTop = 60.px
        }
        ul {
            id = voteListRootId

            css {
                display = Display.grid
                justifyItems = JustifyItems.center
                listStyleType = ListStyleType.none
                padding = 0.px
                margin = 0.px
            }
        }
    }
    div {
        css {
            marginTop = 40.px
            textAlign = TextAlign.center
            display = Display.none
        }
        id = voteAverageRootId

        span {
            css {
                marginRight = 4.px
                fontFamily = "roboto".unsafeCast<FontFamily>()
                fontSize = FontSize.xxLarge
            }
            +"Average: "
        }
        span {
            css {
                fontFamily = "roboto".unsafeCast<FontFamily>()
                fontSize = FontSize.xxLarge
            }
            id = voteAverageValueId
        }
    }
}

private val onMouseOverCard = { event: MouseEvent<HTMLDivElement, *> ->
    val cardElement = event.currentTarget as? HTMLDivElement
    if (cardElement != null) {
        cardElement.dataset[DataAttribute.IS_MOUSE_HIGHLIGHTED] = "${true}"
        updateCardStyle(cardElement)
    }
}

private val onMouseOutCard = { event: MouseEvent<HTMLDivElement, *> ->
    val cardElement = event.currentTarget as? HTMLDivElement
    if (cardElement != null) {
        cardElement.dataset[DataAttribute.IS_MOUSE_HIGHLIGHTED] = "${false}"
        updateCardStyle(cardElement)
    }
}

private fun updateCardStyle(cardElement: HTMLDivElement) {
    val isMouseHighlighted = cardElement.dataset[DataAttribute.IS_MOUSE_HIGHLIGHTED].toBoolean()
    val isVoteHighlighted = cardElement.dataset[DataAttribute.IS_VOTE_HIGHLIGHTED].toBoolean()
    val votesRevealed = LocalData.room?.votesRevealed ?: false

    cardElement.style.backgroundColor =
        if (isVoteHighlighted) {
            if (isMouseHighlighted && !votesRevealed) {
                Color.BLUE_DARK.asString()
            } else {
                Color.BLUE_PRIMARY.asString()
            }
        } else {
            if (isMouseHighlighted && !votesRevealed) {
                Color.GREY_DARK.asString()
            } else {
                Color.GREY_PRIMARY.asString()
            }
        }

    val cardCursorStyle =
        if (votesRevealed) {
            "default"
        } else {
            "pointer"
        }
    cardElement.style.setProperty("cursor", cardCursorStyle)
}

private fun startSync() {
    MainScope().launch {
        sync()
    }
}

fun updateRoomUI() {
    val room = LocalData.room
    val userName = LocalData.userName
    if (room == null || userName == null) {
        return
    }

    val userVote = room.participants.find { it.name == userName }?.vote
    LocalData.voteOptions.entries.forEach { entry ->
        val cardElement = document.getElementById(entry.key) as? HTMLDivElement
        if (cardElement != null) {
            val isVoted = (userVote != null && entry.value == userVote)
            cardElement.dataset[DataAttribute.IS_VOTE_HIGHLIGHTED] = "$isVoted"
            updateCardStyle(cardElement)
        }
    }

    val revealButton = document.getElementById(revealButtonId) as HTMLButtonElement
    val clearButton = document.getElementById(clearButtonId) as HTMLButtonElement

    val hasVotes = room.participants.any { it.vote != null }
    revealButton.disabled = !hasVotes || room.votesRevealed
    clearButton.disabled = !hasVotes || !room.votesRevealed
    updateButtonStyle(revealButton)
    updateButtonStyle(clearButton)

    val voteListElement = document.getElementById(voteListRootId) as HTMLUListElement
    do {
        val firstChild = voteListElement.firstChild
        if (firstChild != null) {
            voteListElement.removeChild(firstChild)
        }
    } while(voteListElement.firstChild != null)

    room.participants.forEach { participant ->
        if (participant.vote != null) {
            val newItem = document.createElement("li") as HTMLLIElement
            voteListElement.appendChild(newItem)
            render(UserVoteItem(participant, room.votesRevealed).create(), newItem)
        }
    }

    val voteAverageRootElement = document.getElementById(voteAverageRootId)  as HTMLDivElement
    if (room.votesRevealed) {
        voteAverageRootElement.style.setProperty("display", "block")

        val voteAverageValueElement = document.getElementById(voteAverageValueId) as HTMLSpanElement
        val validVotes = room.participants.mapNotNull { voteToFloat(it.vote) }
        voteAverageValueElement.innerText = (validVotes.sum()/validVotes.size).toString()
    } else {
        voteAverageRootElement.style.setProperty("display", "none")
    }
}

private fun updateButtonStyle(button: HTMLButtonElement) {
    if (button.disabled) {
        button.style.setProperty("background-color", Color.GREY_PRIMARY.asString())
        button.style.setProperty("color", Color.GREY_DARK.asString())
        button.style.setProperty("cursor", "default")
    } else {
        button.style.setProperty("background-color", Color.BLUE_SECONDARY.asString())
        button.style.setProperty("color", Color.WHITE.asString())
        button.style.setProperty("cursor", "pointer")
    }
}

private val voteCardClicked = { event: MouseEvent<HTMLElement, *> ->
    MainScope().launch {
        val votesRevealed = LocalData.room?.votesRevealed ?: false
        if (votesRevealed) {
            return@launch
        }

        val element = event.target as? HTMLElement
        var cardId = element?.id
        if (cardId.isNullOrBlank()) {  // If user click on span instead of div
            cardId = element?.parentElement?.id
        }
        if (cardId.isNullOrBlank()) {
            console.log("Could not find card ID")
            return@launch
        }

        val vote = LocalData.voteOptions[cardId]
            ?: run {
                console.log("Could not find vote value")
                return@launch
            }
        sendVote(vote)
    }
    Unit
}

private val revealVotesButtonClicked = { _: MouseEvent<HTMLElement, *> ->
    MainScope().launch {
        revealVotes()
    }
    Unit
}

private val clearVotesButtonClicked = { _: MouseEvent<HTMLElement, *> ->
    MainScope().launch {
        clearVotes()
    }
    Unit
}