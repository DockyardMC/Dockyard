package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Experience")
@ClientboundPacketInfo(0x5C, ProtocolState.PLAY)
class ClientboundSetExperiencePacket(bar: Float, level: Int): ClientboundPacket() {

    init {
        data.writeFloat(bar)
        data.writeVarInt(level)
        data.writeVarInt(0)
    }

}