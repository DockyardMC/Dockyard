package io.github.dockyardmc.config

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import log
import java.io.File
import java.lang.Exception

object ConfigManager {

    val defaultConfig = DockyardConfig(
        true,
        25565,
        20f,
        true
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
    val includeDockyardExtras: Boolean,
    val port: Int,
    val defaultTickRate: Float,
    val keepAliveEnabled: Boolean
)