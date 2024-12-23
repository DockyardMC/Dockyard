package io.github.dockyardmc.motd

import cz.lukynka.Bindable
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.Branding
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URL
import java.util.*

object ServerStatusManager {

    private lateinit var cache: ServerStatus

    private val iconFile = File("./icon.png")

    private val base64EncodedIcon = Bindable<String>(
        if(iconFile.exists()) Base64.getEncoder().encode(iconFile.readBytes()).decodeToString()
        else ""
    )

    val description = Bindable<String>("${Branding.logo} <gray>Custom Kotlin Server Implementation")

    init {
        description.valueChanged { updateCache() }
        base64EncodedIcon.valueChanged { updateCache() }
    }

    /**
     * Set the server icon from a file. If the file does not exist, the icon
     * will be cleared.
     *
     * @param file The icon file
     */
    fun setIconFromFile(file: File) {
        base64EncodedIcon.value =
            if (file.exists()) Base64.getEncoder().encode(file.readBytes()).decodeToString()
            else ""
    }

    /**
     * Set the server icon from a classpath resource
     *
     * @param resource The resource URL
     *
     * @see Class.getResource
     */
    fun setIconFromResource(resource: URL) {
        base64EncodedIcon.value =
            Base64.getEncoder().encode(resource.readBytes()).decodeToString()
    }

    /**
     * Set the server description (MOTD string)
     *
     * @param description The description
     */
    fun setDescription(description: String) {
        this.description.value = description
    }

    fun getCache(): ServerStatus {
        if(!this::cache.isInitialized) updateCache()
        return cache
    }

    fun updateCache() {
        val playersOnline = mutableListOf<ServerListPlayer>()
        PlayerManager.players.toList().forEach {
            playersOnline.add(ServerListPlayer(it.username, it.uuid.toString()))
        }

        cache = ServerStatus(
            version = Version(
                name = DockyardServer.minecraftVersion.versionName,
                protocol = DockyardServer.minecraftVersion.protocolId,
            ),
            players = Players(
                max = ConfigManager.config.maxPlayers,
                online = PlayerManager.players.size,
                sample = playersOnline,
            ),
            description = description.value.toComponent(),
            enforceSecureChat = false,
            previewsChat = false,
            favicon = "data:image/png;base64,${base64EncodedIcon.value}"
        )
    }
    private val json = getCache().toJson()
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