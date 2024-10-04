package io.github.dockyardmc.particles

import io.github.dockyardmc.registry.Particles
import io.netty.buffer.ByteBuf

interface ParticleData {

    var id: Int

    fun write(byteBuf: ByteBuf)

    companion object {
        fun requiresData(particleId: Int): Boolean {
            return particleId ==  Particles.BLOCK.getProtocolId() ||
                    particleId == Particles.BLOCK_MARKER.getProtocolId() ||
                    particleId == Particles.DUST.getProtocolId() ||
                    particleId == Particles.DUST_COLOR_TRANSITION.getProtocolId() ||
                    particleId == Particles.FALLING_DUST.getProtocolId() ||
                    particleId == Particles.SCULK_CHARGE.getProtocolId() ||
                    particleId == Particles.ITEM.getProtocolId() ||
                    particleId == Particles.VIBRATION.getProtocolId() ||
                    particleId == Particles.SHRIEK.getProtocolId()
        }
    }
}