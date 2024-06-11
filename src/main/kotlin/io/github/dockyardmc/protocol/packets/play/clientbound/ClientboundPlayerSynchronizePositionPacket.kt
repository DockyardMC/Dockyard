package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import java.util.concurrent.atomic.AtomicInteger

class ClientboundPlayerSynchronizePositionPacket(location: Location): ClientboundPacket(0x3E, ProtocolState.PLAY) {

    companion object {
        val teleportId = AtomicInteger()
    }

    init {
        data.writeDouble(location.x)
        data.writeDouble(location.y)
        data.writeDouble(location.z)
        data.writeFloat(location.yaw)
        data.writeFloat(location.pitch)
        data.writeByte(0x0)
        data.writeVarInt(teleportId.incrementAndGet())
    }
}