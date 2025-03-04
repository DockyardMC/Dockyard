package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUUIDArray
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import java.util.*

@WikiVGEntry ("Player Info Remove")
@ClientboundPacketInfo(0x3D, ProtocolState.PLAY)
class ClientboundPlayerInfoRemovePacket(uuidss: List<UUID>): ClientboundPacket() {
    constructor(player: Player) : this(mutableListOf(player).map { it.uuid })
    constructor(uuid: UUID) : this(mutableListOf(uuid))

    init {
        data.writeUUIDArray(uuidss)
    }

}