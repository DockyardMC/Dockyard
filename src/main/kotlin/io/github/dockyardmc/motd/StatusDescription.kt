package io.github.dockyardmc.motd

import kotlinx.serialization.Serializable

@Serializable
class StatusDescription(
    val text: String,
    val color: String?,
    val extra: MutableList<StatusDescription>? = null
)