package io.github.dockyardmc.config

import io.github.dockyardmc.DockyardServer

class Config {
    var ip: String = "0.0.0.0"
    var port: Int = 25565
    var useMojangAuth: Boolean = true
    var networkCompressionThreshold: Int = 256
    var debug: Boolean = false
    var maxPlayers: Int = 50
    var implementationConfig: ImplementationConfig = ImplementationConfig()
    var updateChecker: Boolean = true

    fun withUpdateChecker(updateChecker: Boolean) {
        this.updateChecker = updateChecker
    }

    fun withIp(ip: String) {
        this.ip = ip
    }

    fun withPort(port: Int) {
        this.port = port
    }

    fun useMojangAuth(useMojangAuth: Boolean) {
        this.useMojangAuth = useMojangAuth
    }

    fun withNetworkCompressionThreshold(threshold: Int) {
        this.networkCompressionThreshold = threshold
    }

    fun withMaxPlayers(maxPlayers: Int) {
        this.maxPlayers = maxPlayers
    }

    fun withImplementations(implementationConfigBuilder: ImplementationConfig.() -> Unit) {
        implementationConfigBuilder.invoke(implementationConfig)
    }

    fun useDebugMode(debugMode: Boolean) {
        this.debug = debugMode
    }
}

class ImplementationConfig {
    var applyBlockPlacementRules: Boolean = true
    var notifyUserOfExceptionDuringCommand: Boolean = true
    var commandErrorPrefix: String = "<dark_red>Error <dark_gray>| <red>"
    var commandNoPermissionsMessage: String = "You do not have permissions to execute this command!"
    var defaultEntityViewDistanceBlocks: Int = 64
    var defaultCommands: Boolean = true
    var npcCommand: Boolean = false
    var spark: Boolean = true
    var itemDroppingAndPickup: Boolean = true
}


object ConfigManager {
    var config: Config = DockyardServer.instance.config
}
