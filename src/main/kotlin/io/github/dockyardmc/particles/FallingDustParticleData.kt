package io.github.dockyardmc.particles

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Particles
import io.netty.buffer.ByteBuf

class FallingDustParticleData(val block: io.github.dockyardmc.world.block.Block): ParticleData {

    override var id: Int = Particles.FALLING_DUST.getProtocolId()

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeVarInt(block.getProtocolId())
    }
}