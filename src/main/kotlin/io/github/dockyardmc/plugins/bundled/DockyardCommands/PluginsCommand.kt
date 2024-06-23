package io.github.dockyardmc.plugins.bundled.DockyardCommands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.plugins.PluginManager.loadedPlugins

class PluginsCommand {

    init {
        Commands.add("/plugins") {
            it.description = "Shows list of plugins"
            it.aliases.add("pl")
            it.aliases.add("plugin")
            it.aliases.add("extensions")

            it.execute { executor ->
                val pluginsSize = when(loadedPlugins.size) {
                    0 -> "are <red>no plugins</red>"
                    1 -> "is <lime>1 plugin</lime>"
                    else -> "are <lime>${loadedPlugins.size} plugins</lime>"
                }
                val message = buildString {
                    appendLine()
                    appendLine("<aqua>DockyardMC <dark_gray>| <gray>There $pluginsSize<gray> loaded:")
                    loadedPlugins.forEach { plugin ->
                        appendLine("<gray>  - <yellow>${plugin.name} <gray>version <orange>${plugin.version}<gray> by <aqua>${plugin.author}")
                    }
                }
                executor.sendMessage(message)
            }
        }
    }
}