package io.github.dockyardmc.plugins

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import java.io.File

object PluginManager {

    var loadedPlugins = mutableListOf<DockyardPlugin>()

    @Deprecated("Plugins will be removed in future versions")
    fun loadLocal(plugin: DockyardPlugin) {
        try {
            plugin.load(DockyardServer.instance)
            loadedPlugins.add(plugin)
            log("Loaded plugin ${plugin.name} version ${plugin.version} by ${plugin.author}", LogType.SUCCESS)
        } catch (ex: Exception) {
            log("Error while loading plugin ${plugin.name} version ${plugin.version} by ${plugin.author}: ${ex.message}")
            log(ex)
        }
    }

    @Deprecated("Plugins will be removed in future versions")
    fun unloadLocal(plugin: DockyardPlugin) {
        try {
            plugin.unload(DockyardServer.instance)
            loadedPlugins.remove(plugin)
            log("Loaded plugin ${plugin.name} version ${plugin.version} by ${plugin.author}", LogType.SUCCESS)
        } catch (ex: Exception) {
            log("Error while loading plugin ${plugin.name} version ${plugin.version} by ${plugin.author}: ${ex.message}")
            log(ex)
        }
    }

    fun loadJar(plugin: File) {
        //TODO
    }
}