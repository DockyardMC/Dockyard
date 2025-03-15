package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket


class ClientboundWorldEventPacket(event: WorldEvent, location: Location, extraData: Int, disableRelativeVolume: Boolean) : ClientboundPacket() {

    init {
        buffer.writeInt(event.id)

        val blockX = location.getBlockLocation().x.toInt()
        val blockY = location.getBlockLocation().y.toInt()
        val blockZ = location.getBlockLocation().z.toInt()

        val longPos = ((blockX.toLong() and 0x3FFFFFFL) shl 38) or
                ((blockZ.toLong() and 0x3FFFFFFL) shl 12) or
                (blockY.toLong() and 0xFFFL)

        buffer.writeLong(longPos)
        buffer.writeInt(extraData)
        buffer.writeBoolean(disableRelativeVolume)
    }
}


enum class WorldEvent(val id: Int) {
    PLAY_RECORD(1010)
}