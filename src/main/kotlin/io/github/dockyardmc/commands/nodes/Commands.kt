package io.github.dockyardmc.commands.nodes

import io.github.dockyardmc.DockyardServer

object Commands {

    val commands: MutableMap<String, Command> = mutableMapOf()

    var autoCorrectCaseSensitivity: Boolean = false
    var warnAboutCaseSensitivity: Boolean = true
    var warnWithClosestMatchToInvalidCommand: Boolean = false

    fun add(name: String, command: (Command) -> Unit) {

        val sanitizedName = name.lowercase().removePrefix("/")
        val builder = Command()
        command(builder)
        val finalCommand = builder.build()

        // add aliases as well
        val list = finalCommand.aliases
        list.add(sanitizedName)

        list.forEach { commands[it] = finalCommand }
    }

    init {
        add("version") {
            it.aliases.add("ver")
            it.aliases.add("info")
            it.aliases.add("server")
            it.aliases.add("dockyard")
            it.internalExecutorDoNotUse = { executor ->
                val message = "<aqua>DockyardMC <dark_gray>| <gray>This server is running <yellow>DockyardMC ${DockyardServer.versionInfo.dockyardVersion}<gray>. A custom Minecraft server implementation in Kotlin. <aqua><hover|'<aqua>https://github.com/DockyardMC/Dockyard'><click|open_url|https://github.com/DockyardMC/Dockyard>[Github]<reset>"
                if(executor.isPlayer) {
                    executor.player!!.sendMessage(message)
                } else {
                    executor.console!!.sendMessage(message)
                }
            }
        }
    }
}