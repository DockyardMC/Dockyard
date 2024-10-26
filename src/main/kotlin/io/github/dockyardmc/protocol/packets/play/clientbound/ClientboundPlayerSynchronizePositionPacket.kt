package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.utils.vectors.Vector3d
import io.github.dockyardmc.utils.vectors.writeVector3d
import java.util.concurrent.atomic.AtomicInteger

@WikiVGEntry("Synchronize Player Position")
@ClientboundPacketInfo(0x40, ProtocolState.PLAY)
class ClientboundPlayerSynchronizePositionPacket(location: Location): ClientboundPacket() {

    companion object {
        val teleportId = AtomicInteger()
    }

    init {
        data.writeVarInt(teleportId.incrementAndGet())
        data.writeVector3d(location.toVector3d())
        data.writeVector3d(Vector3d())
        data.writeFloat(location.yaw)
        data.writeFloat(location.pitch)
        data.writeInt(0)
    }
}