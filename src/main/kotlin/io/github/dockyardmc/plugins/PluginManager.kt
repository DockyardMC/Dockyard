package io.github.dockyardmc.plugins

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.Main
import io.github.dockyardmc.commands.Commands
import java.io.File

object PluginManager {

    var loadedPlugins = mutableListOf<DockyardPlugin>()

    fun loadLocal(plugin: DockyardPlugin) {
        try {
            plugin.load(Main.instance)
            loadedPlugins.add(plugin)
            log("Loaded plugin ${plugin.name} version ${plugin.version} by ${plugin.author}", LogType.SUCCESS)
        } catch (ex: Exception) {
            log("Error while loading plugin ${plugin.name} version ${plugin.version} by ${plugin.author}: ${ex.message}")
            log(ex)
        }
    }

    fun unloadLocal(plugin: DockyardPlugin) {
        try {
            plugin.unload(Main.instance)
            loadedPlugins.remove(plugin)
            log("Loaded plugin ${plugin.name} version ${plugin.version} by ${plugin.author}", LogType.SUCCESS)
        } catch (ex: Exception) {
            log("Error while loading plugin ${plugin.name} version ${plugin.version} by ${plugin.author}: ${ex.message}")
            log(ex)
        }
    }

    init {
        Commands.add("/plugins") {
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

                when(executor.isPlayer) {
                    true -> executor.player!!.sendMessage(message)
                    else -> executor.console!!.sendMessage(message)
                }
            }
        }
    }

    fun loadJar(plugin: File) {
        //TODO
    }
}