package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Unload Chunk")
@ClientboundPacketInfo(0x21, ProtocolState.PLAY)
class ClientboundUnloadChunkPacket(chunkX: Int, chunkZ: Int): ClientboundPacket() {

    init {
        data.writeInt(chunkX)
        data.writeInt(chunkZ)
    }

}