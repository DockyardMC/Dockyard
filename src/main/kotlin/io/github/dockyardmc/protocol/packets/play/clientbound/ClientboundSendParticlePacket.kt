package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.location.writeLocationWithoutRot
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.registry.Particle
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.utils.writeVector3f

class ClientboundSendParticlePacket(
    location: Location,
    particle: Particle,
    offset: Vector3f,
    speed: Float,
    count: Int,
    longDistance: Boolean = false,
): ClientboundPacket(0x29) {

    init {
        data.writeVarInt(particle.id)
        data.writeBoolean(longDistance)
        data.writeLocationWithoutRot(location)
        data.writeVector3f(offset)
        data.writeFloat(speed)
        data.writeInt(count)
        //TODO: data
    }

}