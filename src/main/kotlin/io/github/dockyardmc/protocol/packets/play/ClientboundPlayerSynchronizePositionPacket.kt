package io.github.dockyardmc.protocol.packets.play

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPlayerSynchronizePositionPacket(location: Location, teleportId: Int): ClientboundPacket(62) {

    init {
        data.writeDouble(location.x)
        data.writeDouble(location.y)
        data.writeDouble(location.z)
        data.writeFloat(location.yaw)
        data.writeFloat(location.pitch)
        data.writeByte(0)
        data.writeVarInt(teleportId)
    }

}