package io.github.dockyardmc.particles

import io.github.dockyardmc.registry.Particles
import io.netty.buffer.ByteBuf

interface ParticleData {

    var id: Int

    fun write(byteBuf: ByteBuf)

    companion object {
        fun requiresData(particleId: Int): Boolean {
            return particleId ==  Particles.BLOCK.protocolId ||
                    particleId == Particles.BLOCK_MARKER.protocolId ||
                    particleId == Particles.DUST.protocolId ||
                    particleId == Particles.DUST_COLOR_TRANSITION.protocolId ||
                    particleId == Particles.FALLING_DUST.protocolId ||
                    particleId == Particles.SCULK_CHARGE.protocolId ||
                    particleId == Particles.ITEM.protocolId ||
                    particleId == Particles.VIBRATION.protocolId ||
                    particleId == Particles.SHRIEK.protocolId
        }
    }
}