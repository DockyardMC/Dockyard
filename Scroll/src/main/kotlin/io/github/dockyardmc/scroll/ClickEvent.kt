package io.github.dockyardmc.scroll

import kotlinx.serialization.Serializable

@Serializable
class ClickEvent(
    val action: ClickAction,
    val value: String? = null
)