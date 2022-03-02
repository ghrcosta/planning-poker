package components.room

import Participant
import components.Color
import csstype.AlignItems
import csstype.AlignSelf
import csstype.Cursor
import csstype.Display
import csstype.Flex
import csstype.FontFamily
import csstype.FontSize
import csstype.TextAlign
import csstype.px
import react.FC
import react.Props
import react.ReactElement
import react.create
import react.css.css
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span

class UserVoteItem(private val participant: Participant, private val revealed: Boolean) {
    fun create(): ReactElement {
        return userVoteItemComponent.create()
    }

    private val userVoteItemComponent = FC<Props> {
        div {
            css {
                display = Display.flex
                alignItems = AlignItems.center
                cursor = "default".unsafeCast<Cursor>()
            }
            span {
                css {
                    marginRight = 16.px
                    fontFamily = "roboto".unsafeCast<FontFamily>()
                    fontSize = FontSize.xLarge
                }

                +participant.name
            }
            div {
                css {
                    display = Display.flex
                    width = 35.px
                    height = 52.px
                    backgroundColor = Color.GREY_PRIMARY.asBackgroundColorObj()
                }

                span {
                    css {
                        flex = "1".unsafeCast<Flex>()
                        textAlign = TextAlign.center
                        alignSelf = AlignSelf.center
                        fontFamily = FontFamily.monospace
                        fontSize = FontSize.xLarge
                    }
                    +if (revealed) participant.vote!! else ""
                }
            }
        }
    }
}