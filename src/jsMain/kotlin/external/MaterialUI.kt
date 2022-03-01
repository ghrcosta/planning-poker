@file:JsModule("@mui/material")
@file:JsNonModule
@file:Suppress("unused")

package external

import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import react.ComponentClass
import react.Props
import react.dom.events.ChangeEvent
import react.dom.events.MouseEvent


@JsName("Button")
external val MaterialButton: ComponentClass<MaterialButtonProps>
external interface MaterialButtonProps : Props {
    var variant: String?

    var size: String?

    var color: String?

    var disableElevation: Boolean?

    var onClick: (MouseEvent<HTMLButtonElement, *>) -> Unit
}

// https://mui.com/pt/api/text-field/
@JsName("TextField")
external val MaterialTextField: ComponentClass<MaterialTextFieldProps>
external interface MaterialTextFieldProps : Props {
    var id: String?
    var variant: String?
    var type: String?
    var label: String?
    var error: Boolean?
    var required: Boolean?
    var onChange: (ChangeEvent<HTMLInputElement>) -> Unit
}