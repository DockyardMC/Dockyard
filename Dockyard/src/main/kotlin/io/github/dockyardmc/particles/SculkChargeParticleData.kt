package io.github.dockyardmc.particles

import io.github.dockyardmc.registry.Particles
import io.netty.buffer.ByteBuf

class SculkChargeParticleData(val roll: Float): ParticleData {

    override var id: Int = Particles.SCULK_CHARGE.getProtocolId()

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeFloat(roll)
    }
}