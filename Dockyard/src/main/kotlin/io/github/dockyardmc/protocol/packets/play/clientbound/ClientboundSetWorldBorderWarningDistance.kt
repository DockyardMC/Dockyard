package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Border Warning Distance")
@ClientboundPacketInfo(0x51, ProtocolState.PLAY)
class ClientboundSetWorldBorderWarningDistance(distance: Int): ClientboundPacket() {

    init {
        data.writeVarInt(distance)
    }

}