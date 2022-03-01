package components.room

import csstype.Position
import csstype.pct
import csstype.translate
import react.FC
import react.Props
import react.css.css
import react.dom.html.ReactHTML.div

val RoomComponent = FC<Props> {

    div {
        css {
            position = Position.absolute
            top = 50.pct
            left = 50.pct
            transform = translate(tx = (-50).pct, ty = (-50).pct)
        }

        AddParticipantComponent {

        }
    }
}