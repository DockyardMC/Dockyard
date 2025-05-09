package io.github.dockyardmc.particles.data

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.world.block.Block
import io.netty.buffer.ByteBuf

data class BlockMarkerParticleData(val block: Block) : ParticleData {

    override var id: Int = Particles.BLOCK_MARKER.getProtocolId()

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(block.getProtocolId())
    }
}