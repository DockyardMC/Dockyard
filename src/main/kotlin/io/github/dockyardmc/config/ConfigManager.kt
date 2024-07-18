package io.github.dockyardmc.config

import com.akuleshov7.ktoml.Toml
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File
import java.lang.Exception

object ConfigManager {

    val defaultConfig = DockyardConfig(
        serverConfig = ServerConfig(
            ip = "0.0.0.0",
            port = 25565,
            networkCompressionThreshold = 256,
            debug = false
        ),
        chunkEngine = ChunkEngine(
            async = true,
            threadPerNewChunk = true,
            maxThreads = 25
        ),
        bundledPlugins = BundledPlugins(
            dockyardCommands = false,
            dockyardExtras = false,
            mayaTestPlugin = false,
            mudkipTestPlugin = false,
            emberSeeker = false
        )
    )

    val configFile = File("./dockyard.toml")

    var currentConfig: DockyardConfig = defaultConfig

    fun load() {
        log("Loading dockyard config file..", LogType.CONFIG)

        if(!configFile.exists()) {
            log("File ./dockyard.toml does not exist, creating new one with default values!", LogType.CONFIG)
            val text = Toml.encodeToString<DockyardConfig>(defaultConfig)
            configFile.writeText(text)
        }

        try {
            val config = Toml.decodeFromString<DockyardConfig>(configFile.readText())
            currentConfig = config
            log("Dockyard Config File has been loaded successfully!", LogType.SUCCESS)
        } catch (ex: Exception) {
            log("There was an error while loading your dockyard config file, the default config values will be used instead.", LogType.ERROR)
            log(ex)
            currentConfig = defaultConfig
        }
    }
}

@Serializable
data class DockyardConfig(
    val serverConfig: ServerConfig,
    val chunkEngine: ChunkEngine,
    val bundledPlugins: BundledPlugins
)

@Serializable
data class ServerConfig(
    val ip: String,
    val port: Int,
    val networkCompressionThreshold: Int,
    val debug: Boolean
)

@Serializable
data class ChunkEngine(
    val async: Boolean,
    val threadPerNewChunk: Boolean,
    val maxThreads: Int
)

@Serializable
data class BundledPlugins(
    var dockyardCommands: Boolean,
    var dockyardExtras: Boolean,
    var mayaTestPlugin: Boolean,
    var mudkipTestPlugin: Boolean,
    var emberSeeker: Boolean
)