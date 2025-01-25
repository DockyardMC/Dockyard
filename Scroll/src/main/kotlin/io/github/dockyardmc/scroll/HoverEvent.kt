package io.github.dockyardmc.scroll

import kotlinx.serialization.Serializable

@Serializable
class HoverEvent(
    val action: HoverAction,
    val contents: Component? = null
)