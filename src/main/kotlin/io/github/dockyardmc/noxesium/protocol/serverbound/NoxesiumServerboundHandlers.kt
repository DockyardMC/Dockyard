package io.github.dockyardmc.noxesium.protocol.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.noxesium.NoxesiumClientInformationEvent
import io.github.dockyardmc.events.noxesium.NoxesiumClientSettingsEvent
import io.github.dockyardmc.events.noxesium.NoxesiumQibTriggeredEvent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.utils.getPlayerEventContext

object NoxesiumServerboundHandlers {

    fun handleClientInfo(player: Player, packet: ServerboundNoxesiumClientInformationPacket) {
        Events.dispatch(NoxesiumClientInformationEvent(player, packet.protocolVersion, packet.versionString, getPlayerEventContext(player)))
    }

    fun handleClientSettings(player: Player, packet: ServerboundNoxesiumClientSettingsPacket) {
        Events.dispatch(NoxesiumClientSettingsEvent(player, packet.clientSettings, getPlayerEventContext(player)))
    }

    fun handleQibTriggered(player: Player, packet: ServerboundNoxesiumQibTriggeredPacket) {
        Events.dispatch(NoxesiumQibTriggeredEvent(player, packet.behaviour, packet.qibType, packet.entityId, getPlayerEventContext(player)))
    }

}