package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.vectors.Vector3d
import io.github.dockyardmc.utils.vectors.writeVector3d
import java.util.concurrent.atomic.AtomicInteger

class ClientboundPlayerSynchronizePositionPacket(location: Location) : ClientboundPacket() {

    companion object {
        val teleportId = AtomicInteger()
    }

    init {
        buffer.writeVarInt(teleportId.incrementAndGet())
        buffer.writeVector3d(location.toVector3d())
        buffer.writeVector3d(Vector3d())
        buffer.writeFloat(location.yaw)
        buffer.writeFloat(location.pitch)
        buffer.writeInt(0)
    }
}