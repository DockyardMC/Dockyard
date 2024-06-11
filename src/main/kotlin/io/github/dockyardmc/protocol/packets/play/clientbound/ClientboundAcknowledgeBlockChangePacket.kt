package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundAcknowledgeBlockChangePacket(val sequenceId: Int): ClientboundPacket(0x05, ProtocolState.PLAY) {

    init {
        data.writeVarInt(sequenceId)
    }

}