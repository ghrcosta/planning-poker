@file:JsModule("@mui/material")
@file:JsNonModule

package external

import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLTextAreaElement
import react.ComponentClass
import react.Props
import react.dom.events.KeyboardEvent
import react.dom.events.MouseEvent


// https://mui.com/pt/api/button/
@JsName("Button")
external val MaterialButton: ComponentClass<MaterialButtonProps>
external interface MaterialButtonProps : Props {
    var id: String?
    var variant: String?
    var size: String?
    var disableElevation: Boolean?
    var onClick: (MouseEvent<HTMLButtonElement, *>) -> Unit
}


// https://mui.com/pt/api/text-field/
@JsName("TextField")
external val MaterialTextField: ComponentClass<MaterialTextFieldProps>
external interface MaterialTextFieldProps : Props {
    var id: String?
    var variant: String?
    var label: String?
    var required: Boolean?
    var onKeyUp: (KeyboardEvent<HTMLTextAreaElement>) -> Unit
}