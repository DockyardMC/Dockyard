package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUUIDArray
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry ("Player Info Remove")
@ClientboundPacketInfo(0x3D, ProtocolState.PLAY)
class ClientboundPlayerInfoRemovePacket(players: MutableList<Player>): ClientboundPacket() {
    constructor(player: Player) : this(mutableListOf(player))

    init {
        data.writeUUIDArray(players.map { it.uuid })
    }

}