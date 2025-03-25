package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.maths.vectors.Vector3d
import java.util.concurrent.atomic.AtomicInteger

class ClientboundPlayerSynchronizePositionPacket(location: Location) : ClientboundPacket() {

    companion object {
        val teleportId = AtomicInteger()
    }

    init {
        buffer.writeVarInt(teleportId.incrementAndGet())
        location.toVector3d().write(buffer)
        Vector3d().write(buffer)
        buffer.writeFloat(location.yaw)
        buffer.writeFloat(location.pitch)
        buffer.writeInt(0)
    }
}