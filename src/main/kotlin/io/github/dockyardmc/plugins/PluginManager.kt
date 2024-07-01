package io.github.dockyardmc.plugins

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.Main
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

    }

    fun loadJar(plugin: File) {
        //TODO
    }
}