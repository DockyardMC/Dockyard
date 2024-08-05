package io.github.dockyardmc.particles

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Particles
import io.netty.buffer.ByteBuf

class BlockParticleData(val block: Block): ParticleData {

    override var id: Int = Particles.BLOCK.id

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeVarInt(block.getId())
    }
}