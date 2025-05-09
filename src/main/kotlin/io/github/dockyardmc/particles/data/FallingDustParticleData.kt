package io.github.dockyardmc.particles.data

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Particles
import io.netty.buffer.ByteBuf

class FallingDustParticleData(val block: io.github.dockyardmc.world.block.Block): ParticleData {

    override var id: Int = Particles.FALLING_DUST.getProtocolId()

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(block.getProtocolId())
    }
}