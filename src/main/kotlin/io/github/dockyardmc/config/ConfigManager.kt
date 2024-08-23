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
        configVersion = 2,
        serverConfig = ServerConfig(),
        chunkEngine = ChunkEngine(),
        bundledPlugins = BundledPlugins(),
        implementationConfig = ImplementationConfig()
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
            log("There was an error while loading your dockyard config file, the default config values will be used instead. Please check if your config format is up to date!", LogType.FATAL)
            log(ex)
            currentConfig = defaultConfig
        }
    }
}

@Serializable
data class DockyardConfig(
    val configVersion: Int,
    val serverConfig: ServerConfig,
    val chunkEngine: ChunkEngine,
    val implementationConfig: ImplementationConfig,
    val bundledPlugins: BundledPlugins
)

@Serializable
data class ServerConfig(
    val ip: String = "0.0.0.0",
    val port: Int = 25565,
    val networkCompressionThreshold: Int = 256,
    val debug: Boolean = false
)

@Serializable
data class ChunkEngine(
    val async: Boolean = true,
)

@Serializable
data class ImplementationConfig(
    val applyBlockPlacementRules: Boolean = true
)

@Serializable
data class BundledPlugins(
    var dockyardCommands: Boolean = true,
    var dockyardExtras: Boolean = false,
)