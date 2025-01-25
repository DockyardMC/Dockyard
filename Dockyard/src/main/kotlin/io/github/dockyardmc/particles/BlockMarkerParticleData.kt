package io.github.dockyardmc.particles

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Particles
import io.netty.buffer.ByteBuf

class BlockMarkerParticleData(val block: Block): ParticleData {

    override var id: Int = Particles.BLOCK_MARKER.getProtocolId()

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeVarInt(block.getProtocolId())
    }
}