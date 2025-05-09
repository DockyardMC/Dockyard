package io.github.dockyardmc.particles.data

import io.github.dockyardmc.extentions.getPackedInt
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.registries.Particle
import io.github.dockyardmc.scroll.CustomColor
import io.netty.buffer.ByteBuf

class DustParticleData(val color: CustomColor, val scale: Float = 1f) : ParticleData {

    constructor(hex: String, scale: Float = 1f) : this(CustomColor.fromHex(hex), scale)

    override val parentParticle: Particle = Particles.DUST

    override fun write(buffer: ByteBuf) {
        buffer.writeInt(color.getPackedInt())
        buffer.writeFloat(scale)
    }
}