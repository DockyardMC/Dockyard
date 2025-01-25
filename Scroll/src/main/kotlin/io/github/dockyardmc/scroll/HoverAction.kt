package io.github.dockyardmc.scroll

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HoverAction {
    @SerialName("show_text")
    SHOW_TEXT,
    @SerialName("show_item")
    SHOW_ITEM
}