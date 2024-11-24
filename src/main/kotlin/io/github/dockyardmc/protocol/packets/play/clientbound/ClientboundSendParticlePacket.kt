package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.particles.ParticleData
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.registry.registries.Particle
import io.github.dockyardmc.utils.vectors.Vector3f
import io.github.dockyardmc.utils.vectors.writeVector3f

@WikiVGEntry("Particle")
@ClientboundPacketInfo(0x29, ProtocolState.PLAY)
class ClientboundSendParticlePacket(
    location: Location,
    particle: Particle,
    offset: Vector3f,
    speed: Float,
    count: Int,
    longDistance: Boolean = false,
    particleData: ParticleData?
): ClientboundPacket() {

    init {
        if(particleData != null && particleData.id != particle.getProtocolId()) throw Exception("Particle data ${particleData::class.simpleName} is not valid for particle ${particle.identifier}")
        if(particleData == null && ParticleData.requiresData(particle.getProtocolId())) throw Exception("Particle ${particle.identifier} requires particle data")

        data.writeBoolean(longDistance)
        data.writeLocation(location)
        data.writeVector3f(offset)
        data.writeFloat(speed)
        data.writeInt(count)
        data.writeVarInt(particle.getProtocolId())
        particleData?.write(data)
    }
}