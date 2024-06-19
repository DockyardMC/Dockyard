package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Center Chunk")
@ClientboundPacketInfo(0x54, ProtocolState.PLAY)
class ClientboundSetCenterChunkPacket(chunkX: Int, chunkZ: Int): ClientboundPacket() {

    init {
        data.writeVarInt(chunkX)
        data.writeVarInt(chunkZ)
    }

}