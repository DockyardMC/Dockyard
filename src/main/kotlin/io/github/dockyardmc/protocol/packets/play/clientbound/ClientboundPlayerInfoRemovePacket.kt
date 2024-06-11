package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeUUIDArray
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundPlayerInfoRemovePacket(players: MutableList<Player>): ClientboundPacket(0x3B, ProtocolState.PLAY) {
    constructor(player: Player) : this(mutableListOf(player))

    init {
        data.writeUUIDArray(players.map { it.uuid })
    }

}