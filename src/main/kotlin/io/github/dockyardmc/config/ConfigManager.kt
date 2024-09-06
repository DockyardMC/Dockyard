package io.github.dockyardmc.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlInputConfig
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File
import java.lang.Exception

object ConfigManager {

    val defaultConfig = DockyardConfig(
        configVersion = 4,
        serverConfig = ServerConfig(),
        chunkEngine = ChunkEngine(),
        defaultImplementations = DefaultImplementations(),
        implementationConfig = ImplementationConfig()
    )

    val configFile = File("dockyard.toml")

    var currentConfig: DockyardConfig = defaultConfig
    val toml = Toml(inputConfig = TomlInputConfig(
        ignoreUnknownNames = true,
        allowNullValues = false
    ))

    fun load() {
        log("Loading dockyard config file..", LogType.CONFIG)

        if(!configFile.exists()) save(defaultConfig)

        try {
            val config = toml.decodeFromString<DockyardConfig>(configFile.readText())
            currentConfig = config
            log("Dockyard Config File has been loaded successfully!", LogType.SUCCESS)
            if(currentConfig.configVersion != defaultConfig.configVersion) {
                currentConfig.configVersion = defaultConfig.configVersion
                save(currentConfig)
            }
        } catch (ex: Exception) {
            log("There was an error while loading your dockyard config file, the default config values will be used instead. Please check if your config format is up to date!", LogType.FATAL)
            log(ex)
            currentConfig = defaultConfig
        }
    }

    fun save(config: DockyardConfig) {
        val text = toml.encodeToString<DockyardConfig>(config)
        configFile.writeText(text)
    }
}

@Serializable
data class DockyardConfig(
    var configVersion: Int,
    val serverConfig: ServerConfig,
    val chunkEngine: ChunkEngine,
    val implementationConfig: ImplementationConfig,
    val defaultImplementations: DefaultImplementations
)

@Serializable
data class ServerConfig(
    val ip: String = "0.0.0.0",
    val port: Int = 25565,
    val networkCompressionThreshold: Int = 256,
    val cacheSchematics: Boolean = true,
    val debug: Boolean = false,
    val maxPlayers: Int = 50,
    val defaultEntityRenderDistanceBlocks: Int = 64,
)

@Serializable
data class ChunkEngine(
    val async: Boolean = true,
)

@Serializable
data class ImplementationConfig(
    val applyBlockPlacementRules: Boolean = true,
    val notifyUserOfExceptionDuringCommand: Boolean = true,
    val commandErrorPrefix: String = "<dark_red>Error <dark_gray>| <red>",
    val commandNoPermissionsMessage: String = "You do not have permissions to execute this command!",
)

@Serializable
data class DefaultImplementations(
    var dockyardCommands: Boolean = true,
)