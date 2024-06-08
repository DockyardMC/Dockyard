package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.location.writeLocationWithoutRot
import io.github.dockyardmc.particles.ParticleData
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.registry.Particle
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.utils.writeVector3f

class ClientboundSendParticlePacket(
    location: Location,
    particle: Particle,
    offset: Vector3f,
    speed: Float,
    count: Int,
    longDistance: Boolean = false,
    particleData: ParticleData?
): ClientboundPacket(0x27) {

    init {
        if(particleData != null && particleData.id != particle.id) throw Exception("Particle data ${particleData::class.simpleName} is not valid for particle ${particle.namespace}")
        if(particleData == null && ParticleData.requiresData(particle.id)) throw Exception("Particle ${particle.namespace} requires particle data")

        data.writeVarInt(particle.id)
        data.writeBoolean(longDistance)
        data.writeLocationWithoutRot(location)
        data.writeVector3f(offset)
        data.writeFloat(speed)
        data.writeInt(count)
        particleData?.write(data)
    }
}