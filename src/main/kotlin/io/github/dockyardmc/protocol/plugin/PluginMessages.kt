package io.github.dockyardmc.protocol.plugin

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.plugin.messages.BrandPluginMessage
import io.github.dockyardmc.protocol.plugin.messages.PluginMessageHandler
import io.github.dockyardmc.protocol.plugin.messages.RegisterPluginMessage
import io.github.dockyardmc.protocol.plugin.messages.UnregisterPluginMessage
import io.netty.buffer.ByteBuf
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredMemberFunctions

object PluginMessages {
    val channels: MutableMap<String, KClass<out PluginMessageHandler>> = mutableMapOf(
        "minecraft:register" to RegisterPluginMessage::class,
        "minecraft:unregister" to UnregisterPluginMessage::class,
        "minecraft:brand" to BrandPluginMessage::class
    )

    val registeredChannels: MutableList<String> = mutableListOf()

    fun sendRegisteredChannels(player: Player) {
        player.sendPacket(RegisterPluginMessage(registeredChannels).asPlayPacket("minecraft:register"))
    }

    fun handle(channel: String, payload: ByteBuf, player: Player) {

        val pluginMessage = channels[channel]
        if (pluginMessage == null) {
            log("Received plugin message with no handler: $channel", LogType.WARNING)
            payload.release()
            return
        }

        val companionObject = pluginMessage.companionObject ?: throw IllegalStateException("${pluginMessage.simpleName} doesn't have a companion object")
        val readFunction = companionObject.declaredMemberFunctions.find { it.name == "read" } ?: throw IllegalStateException("${pluginMessage.simpleName} doesn't have read(ByteBuf) companion function")

        try {
            val read = readFunction.call(companionObject.objectInstance, payload) as PluginMessageHandler
            read.handle(player)
        } catch (ex: Exception) {
            log("Failed to read plugin message ${pluginMessage.simpleName}:", LogType.ERROR)
            log(ex)
        } finally {
            payload.release()
        }
    }
}