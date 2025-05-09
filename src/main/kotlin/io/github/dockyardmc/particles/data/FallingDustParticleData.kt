package io.github.dockyardmc.particles.data

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.registries.Particle
import io.netty.buffer.ByteBuf

class FallingDustParticleData(val block: io.github.dockyardmc.world.block.Block) : ParticleData {

    override val parentParticle: Particle = Particles.FALLING_DUST

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(block.getProtocolId())
    }
}