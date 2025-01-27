package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.scroll.Component
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun ServerStatus.toJson(): String = Json.encodeToString<ServerStatus>(this)

@Serializable
data class ServerStatus(
    var version: Version,
    var players: Players,
    var description: Component,
    var enforceSecureChat: Boolean,
    var previewsChat: Boolean,
    var favicon: String
)

@Serializable
data class Players(
    var max: Int,
    var online: Int,
    var sample: MutableList<ServerListPlayer>
)

@Serializable
data class Version(
    var name: String,
    var protocol: Int
)

@Serializable
data class ServerListPlayer(
    var name: String,
    var uuid: String
)