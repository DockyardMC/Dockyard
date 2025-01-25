package io.github.dockyardmc.scroll.serializers

import io.github.dockyardmc.scroll.ClickEvent
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.HoverEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.lang.model.type.UnionType

object JsonToComponentSerializer {

    fun serialize(json: String): Component {
        val serializer = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

//        return Json.decodeFromJsonElement(serializer.parseToJsonElement(json))
        return Json.decodeFromString<Component>(json)

    }
}