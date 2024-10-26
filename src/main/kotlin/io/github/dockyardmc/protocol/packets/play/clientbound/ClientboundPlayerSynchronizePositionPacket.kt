package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import java.util.concurrent.atomic.AtomicInteger

@WikiVGEntry("Synchronize Player Position")
@ClientboundPacketInfo(0x40, ProtocolState.PLAY)
class ClientboundPlayerSynchronizePositionPacket(location: Location): ClientboundPacket() {

    companion object {
        val teleportId = AtomicInteger()
    }

    init {
        data.writeVarInt(teleportId.incrementAndGet())
        data.writeDouble(location.x)
        data.writeDouble(location.y)
        data.writeDouble(location.z)
        data.writeFloat(location.yaw)
        data.writeFloat(location.pitch)
        data.writeByte(0x0)
    }
}