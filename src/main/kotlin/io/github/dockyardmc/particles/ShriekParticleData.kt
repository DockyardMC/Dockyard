package io.github.dockyardmc.particles

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Particles
import io.netty.buffer.ByteBuf

class ShriekParticleData(val delayTicks: Int): ParticleData {

    override var id: Int = Particles.SHRIEK.id

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeVarInt(delayTicks)
    }
}