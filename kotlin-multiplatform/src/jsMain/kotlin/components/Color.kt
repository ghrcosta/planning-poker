package components

import csstype.BackgroundColor

// Based on: https://material.io/resources/color/
class Color (private val value: String) {
    companion object {
        val GREY_PRIMARY = Color("#eceff1")
        val GREY_DARK = Color("#babdbe")

        val BLUE_PRIMARY = Color("#29b6f6")
        val BLUE_SECONDARY = Color("#1976d2")
        val BLUE_DARK = Color("#0086c3")

        val WHITE = Color("#FFFFFF")
    }

    fun asString() = value
    fun asBackgroundColorObj() = value.unsafeCast<BackgroundColor>()
}