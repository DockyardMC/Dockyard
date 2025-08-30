package io.github.dockyardmc.noxesium.protocol.serverbound

import cz.lukynka.prettylog.log
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.noxesium.NoxesiumClientInformationEvent
import io.github.dockyardmc.events.noxesium.NoxesiumClientSettingsEvent
import io.github.dockyardmc.events.noxesium.NoxesiumQibTriggeredEvent
import io.github.dockyardmc.events.noxesium.NoxesiumRiptideEvent
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.utils.getPlayerEventContext

internal class NoxesiumServerboundHandlers {

    fun handleClientInfo(packet: ServerboundNoxesiumClientInformationPacket, playerNetworkManager: PlayerNetworkManager) {
        Events.dispatch(NoxesiumClientInformationEvent(playerNetworkManager.player, packet.protocolVersion, packet.versionString, getPlayerEventContext(playerNetworkManager.player)))
    }

    fun handleClientSettings(packet: ServerboundNoxesiumClientSettingsPacket, playerNetworkManager: PlayerNetworkManager) {
        Events.dispatch(NoxesiumClientSettingsEvent(playerNetworkManager.player, packet.clientSettings, getPlayerEventContext(playerNetworkManager.player)))
    }

    fun handleQibTriggered(packet: ServerboundNoxesiumQibTriggeredPacket, playerNetworkManager: PlayerNetworkManager) {
        Events.dispatch(NoxesiumQibTriggeredEvent(playerNetworkManager.player, packet.behaviour, packet.qibType, packet.entityId, getPlayerEventContext(playerNetworkManager.player)))
    }

    fun handleRiptide(packet: ServerboundNoxesiumRiptidePacket, playerNetworkManager: PlayerNetworkManager) {
        Events.dispatch(NoxesiumRiptideEvent(playerNetworkManager.player, packet.slot, getPlayerEventContext(playerNetworkManager.player)))
    }

}