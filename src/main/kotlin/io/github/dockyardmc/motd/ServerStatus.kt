package io.github.dockyardmc.motd

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableMap
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.DockyardBranding
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URL
import java.util.*

object ServerStatusManager {

    private lateinit var defaultCache: ServerStatus
    private val endpointCache: MutableMap<String, ServerStatus> = mutableMapOf()
    val endpointDescriptions: BindableMap<String, String> = BindableMap()
    val endpointIcons: BindableMap<String, ServerIcon> = BindableMap()

    val defaultIcon: Bindable<ServerIcon> = Bindable(ServerIcon.fromFile(File("./icon.png")))

    val defaultDescription = Bindable<String>("${DockyardBranding.logo} <gray>Custom Kotlin Server Implementation")

    init {
        defaultDescription.valueChanged { updateCache() }
        defaultIcon.valueChanged { updateCache() }
        endpointDescriptions.mapUpdated { updateCache() }
        endpointIcons.mapUpdated { updateCache() }
    }

    fun getCache(ip: String?): ServerStatus {
        val endpoint = endpointCache[ip]
        if(ip == null || endpoint == null) {
            if(!::defaultCache.isInitialized) updateCache()
            return defaultCache
        }

        return endpoint
    }

    fun updateCache() {
        val playersOnline = mutableListOf<ServerListPlayer>()
        PlayerManager.players.toList().forEach {
            playersOnline.add(ServerListPlayer(it.username, it.uuid.toString()))
        }

        val version = Version(
            name = DockyardServer.minecraftVersion.versionName,
            protocol = DockyardServer.minecraftVersion.protocolId,
        )

        val players = Players(
            max = ConfigManager.config.maxPlayers,
            online = PlayerManager.players.size,
            sample = playersOnline,
        )

        val favicon = "data:image/png;base64,${defaultIcon.value.base64Encoded}"

        endpointDescriptions.values.forEach { (ip, description) ->
            val icon = endpointIcons[ip]?.base64Encoded ?: defaultIcon.value.base64Encoded
            val status = ServerStatus(
                version = version,
                players = players,
                description = description.toComponent(),
                enforceSecureChat = false,
                previewsChat = false,
                favicon = "data:image/png;base64,${icon}"
            )

            endpointCache[ip] = status
        }

        defaultCache = ServerStatus(
            version = version,
            players = players,
            description = defaultDescription.value.toComponent(),
            enforceSecureChat = false,
            previewsChat = false,
            favicon = favicon
        )
    }

    private val json = getCache(null).toJson()
}

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

data class ServerIcon(
    val base64Encoded: String
) {
    companion object {

        fun fromFile(file: File): ServerIcon {
            return ServerIcon(if (file.exists()) Base64.getEncoder().encode(file.readBytes()).decodeToString() else "")
        }

        fun fromURL(url: URL): ServerIcon {
            return ServerIcon(Base64.getEncoder().encode(url.readBytes()).decodeToString())
        }
    }
}