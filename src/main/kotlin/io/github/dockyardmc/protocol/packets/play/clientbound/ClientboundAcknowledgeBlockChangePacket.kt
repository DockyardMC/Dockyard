package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundAcknowledgeBlockChangePacket(val sequenceId: Int): ClientboundPacket(0x05) {

    init {
        data.writeVarInt(sequenceId)
    }

}