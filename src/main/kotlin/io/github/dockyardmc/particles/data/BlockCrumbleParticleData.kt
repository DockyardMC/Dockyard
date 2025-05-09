package io.github.dockyardmc.particles.data

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.registries.Particle
import io.github.dockyardmc.world.block.Block
import io.netty.buffer.ByteBuf

data class BlockCrumbleParticleData(val block: Block) : ParticleData {

    override val parentParticle: Particle = Particles.BLOCK_CRUMBLE

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(block.getProtocolId())
    }
}