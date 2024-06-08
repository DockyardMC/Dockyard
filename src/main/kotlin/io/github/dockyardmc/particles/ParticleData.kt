package io.github.dockyardmc.particles

import io.github.dockyardmc.registry.Particles
import io.netty.buffer.ByteBuf

interface ParticleData {

    var id: Int

    fun write(byteBuf: ByteBuf)

    companion object {
        fun requiresData(particleId: Int): Boolean {
            return particleId ==  Particles.BLOCK.id ||
                    particleId == Particles.BLOCK_MARKER.id ||
                    particleId == Particles.DUST.id ||
                    particleId == Particles.DUST_COLOR_TRANSITION.id ||
                    particleId == Particles.FALLING_DUST.id ||
                    particleId == Particles.SCULK_CHARGE.id ||
                    particleId == Particles.ITEM.id ||
                    particleId == Particles.VIBRATION.id ||
                    particleId == Particles.SHRIEK.id
        }
    }



}