package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.particles.ParticleData
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.registry.registries.Particle
import io.github.dockyardmc.utils.vectors.Vector3f
import io.github.dockyardmc.utils.vectors.writeVector3f

class ClientboundSendParticlePacket(
    location: Location,
    particle: Particle,
    offset: Vector3f,
    speed: Float,
    count: Int,
    overrideLimiter: Boolean = false,
    longDistance: Boolean = false,
    particleData: ParticleData?
): ClientboundPacket() {

    init {
        if(particleData != null && particleData.id != particle.getProtocolId()) throw Exception("Particle data ${particleData::class.simpleName} is not valid for particle ${particle.identifier}")
        if(particleData == null && ParticleData.requiresData(particle.getProtocolId())) throw Exception("Particle ${particle.identifier} requires particle data")

        buffer.writeBoolean(overrideLimiter)
        buffer.writeBoolean(longDistance)
        buffer.writeLocation(location)
        buffer.writeVector3f(offset)
        buffer.writeFloat(speed)
        buffer.writeInt(count)
        buffer.writeVarInt(particle.getProtocolId())
        particleData?.write(buffer)
    }
}