package io.github.dockyardmc.protocol.plugin

import cz.lukynka.prettylog.log
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.RegisterPluginChannelsEvent
import io.github.dockyardmc.events.UnregisterPluginChannelsEvent
import io.github.dockyardmc.noxesium.Noxesium
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.plugin.messages.RegisterPluginMessage
import io.github.dockyardmc.protocol.plugin.messages.UnregisterPluginMessage
import io.github.dockyardmc.utils.getPlayerEventContext

internal class PlayPluginMessageHandler {

    fun handleRegister(message: RegisterPluginMessage, networkManager: PlayerNetworkManager) {
        val event = RegisterPluginChannelsEvent(networkManager.player, message.channels, getPlayerEventContext(networkManager.player))
        Events.dispatch(event)

        networkManager.player.sendPluginMessage(RegisterPluginMessage(PluginMessageRegistry.getChannels(PluginMessageRegistry.Type.PLAY)))

        // Noxesium integration
        if (ConfigManager.config.implementationConfig.noxesium) {
            if (message.channels.contains("${Noxesium.PACKET_NAMESPACE}:server_info")) {
                Noxesium.addPlayer(event.player)
            }
        }
    }

    fun handleUnregister(message: UnregisterPluginMessage, networkManager: PlayerNetworkManager) {
        val event = UnregisterPluginChannelsEvent(networkManager.player, message.channels, getPlayerEventContext(networkManager.player))
        Events.dispatch(event)
    }

}