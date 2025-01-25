package io.github.dockyardmc.particles

import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.scroll.CustomColor
import io.netty.buffer.ByteBuf

class DustParticleData(val color: CustomColor, val scale: Float = 1f): ParticleData {

    constructor(hex: String, scale: Float = 1f): this(CustomColor.fromHex(hex), scale)

    override var id: Int = Particles.DUST.getProtocolId()

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeFloat(color.r / 255f)
        byteBuf.writeFloat(color.g / 255f)
        byteBuf.writeFloat(color.b / 255f)
        byteBuf.writeFloat(scale)
    }
}