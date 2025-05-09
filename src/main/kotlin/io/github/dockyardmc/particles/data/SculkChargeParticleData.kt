package io.github.dockyardmc.particles.data

import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.registries.Particle
import io.netty.buffer.ByteBuf

class SculkChargeParticleData(val roll: Float) : ParticleData {

    override val parentParticle: Particle = Particles.SCULK_CHARGE

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(roll)
    }
}