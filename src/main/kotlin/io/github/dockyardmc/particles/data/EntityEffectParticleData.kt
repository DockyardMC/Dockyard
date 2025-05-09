package io.github.dockyardmc.particles.data

import io.github.dockyardmc.extentions.writeColor
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.scroll.CustomColor
import io.netty.buffer.ByteBuf

data class EntityEffectParticleData(val color: CustomColor) : ParticleData {
    override var id: Int = Particles.ENTITY_EFFECT.getProtocolId()

    override fun write(buffer: ByteBuf) {
        buffer.writeColor(color)
    }
}