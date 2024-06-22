package io.github.dockyardmc.motd

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.VersionToProtocolVersion
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File
import java.util.*

private val iconFile = File("./icon.png")
val base64EncodedIcon = if(iconFile.exists()) Base64.getEncoder().encode(File("./icon.png").readBytes()).decodeToString() else ""
val defaultMotd = ServerStatus(
    version = Version(
        name = DockyardServer.versionInfo.minecraftVersion,
        protocol = VersionToProtocolVersion.map[DockyardServer.versionInfo.minecraftVersion] ?: 0,
    ),
    players = Players(
        max = 727,
        online = PlayerManager.players.size,
        sample = mutableListOf(),
    ),
    description = "<aqua>DockyardMC <dark_gray>| <gray>Custom Kotlin Server Implementation".toComponent(),
    enforceSecureChat = false,
    previewsChat = false,
    favicon = "data:image/png;base64,$base64EncodedIcon"
)
val json = defaultMotd.toJson()

fun ServerStatus.toJson(): String {
    return Json.encodeToString<ServerStatus>(this)
}

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