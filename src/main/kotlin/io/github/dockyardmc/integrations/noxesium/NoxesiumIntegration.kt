package io.github.dockyardmc.integrations.noxesium

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.events.RegisterPluginChannelsEvent
import io.github.dockyardmc.extentions.removeIfPresent
import io.github.dockyardmc.integrations.noxesium.packets.ClientboundNoxesiumServerInformationPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.plugin.messages.RegisterPluginMessage
import kotlin.reflect.KClass

object NoxesiumIntegration {

    const val PROTOCOL_VERSION = 12

    val clientboundPackets = mutableMapOf<String, KClass<*>>(
        ClientboundNoxesiumServerInformationPacket.CHANNEL to ClientboundNoxesiumServerInformationPacket::class
    )

    val playersUsingNoxesium: MutableList<Player> = mutableListOf()
    val enabled get() = ConfigManager.config.implementationConfig.noxesium

    fun register() {

        Events.on<RegisterPluginChannelsEvent> { event ->
            if(!event.channels.contains(ClientboundNoxesiumServerInformationPacket.CHANNEL)) return@on

            log("${event.player} is using Noxesium", LogType.DEBUG)
            event.player.sendPacket(RegisterPluginMessage(clientboundPackets.keys).asPlayPacket())
            event.player.sendPacket(ClientboundNoxesiumServerInformationPacket(PROTOCOL_VERSION).asPlayPacket())
        }

        Events.on<PlayerLeaveEvent> { event ->
            playersUsingNoxesium.removeIfPresent(event.player)
        }
    }
}