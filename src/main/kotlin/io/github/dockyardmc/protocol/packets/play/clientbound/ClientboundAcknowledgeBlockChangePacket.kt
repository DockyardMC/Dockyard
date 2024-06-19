package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Acknowledge Block Change")
@ClientboundPacketInfo(0x05, ProtocolState.PLAY)
class ClientboundAcknowledgeBlockChangePacket(val sequenceId: Int): ClientboundPacket() {

    init {
        data.writeVarInt(sequenceId)
    }

}