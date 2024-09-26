package io.github.dockyardmc.particles

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.getId
import io.netty.buffer.ByteBuf

class BlockMarkerParticleData(val block: Block): ParticleData {

    override var id: Int = Particles.BLOCK_MARKER.protocolId

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeVarInt(block.getId())
    }
}