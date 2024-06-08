package io.github.dockyardmc.particles

import io.github.dockyardmc.extentions.hexToRGB
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.scroll.RGB
import io.netty.buffer.ByteBuf

class DustParticleData(val rgb: RGB, val scale: Float = 1f): ParticleData {

    constructor(hex: String, scale: Float = 1f): this(hexToRGB(hex), scale)

    override var id: Int = Particles.DUST.id

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeFloat(rgb.r / 255f)
        byteBuf.writeFloat(rgb.g / 255f)
        byteBuf.writeFloat(rgb.b / 255f)
        byteBuf.writeFloat(scale)
    }
}