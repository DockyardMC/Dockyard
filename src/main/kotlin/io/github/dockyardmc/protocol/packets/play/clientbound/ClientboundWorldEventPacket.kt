package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState


@ClientboundPacketInfo(0x28, ProtocolState.PLAY)
class ClientboundWorldEventPacket(event: WorldEvent, location: Location, extraData: Int, disableRelativeVolume: Boolean): ClientboundPacket() {

    init {
        data.writeInt(event.id)

        val blockX = location.centerBlockLocation().x.toInt()
        val blockY = location.centerBlockLocation().y.toInt()
        val blockZ = location.centerBlockLocation().z.toInt()

        val longPos = ((blockX.toLong() and 0x3FFFFFFL) shl 38) or
                ((blockZ.toLong() and 0x3FFFFFFL) shl 12) or
                (blockY.toLong() and 0xFFFL)

        data.writeLong(longPos)
        data.writeInt(extraData)
        data.writeBoolean(disableRelativeVolume)
    }
}


enum class WorldEvent(val id: Int) {
    PLAY_RECORD(1010)
}