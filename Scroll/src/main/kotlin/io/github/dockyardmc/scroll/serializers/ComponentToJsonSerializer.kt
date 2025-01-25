package io.github.dockyardmc.scroll.serializers

import io.github.dockyardmc.scroll.Component
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

object ComponentToJsonSerializer {
    fun serialize(component: Component): String {
        return Json.encodeToJsonElement<Component>(component).toString()
    }
}