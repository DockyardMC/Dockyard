package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Center Chunk")
class ClientboundSetCenterChunkPacket(chunkX: Int, chunkZ: Int): ClientboundPacket(0x52, ProtocolState.PLAY) {

    init {
        data.writeVarInt(chunkX)
        data.writeVarInt(chunkZ)
    }

}