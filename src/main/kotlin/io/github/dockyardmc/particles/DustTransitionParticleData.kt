package io.github.dockyardmc.particles

import io.github.dockyardmc.extentions.hexToRGB
import io.github.dockyardmc.extentions.toRGB
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.scroll.RGB
import io.netty.buffer.ByteBuf
import java.awt.Color

class DustTransitionParticleData(val rgbFrom: RGB, val rgbTo: RGB, val scale: Float = 1f): ParticleData {

    constructor(hexFrom: String, hexTo: String, scale: Float = 1f): this(hexToRGB(hexFrom), hexToRGB(hexTo), scale)

    override var id: Int = Particles.DUST_COLOR_TRANSITION.id

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeFloat(rgbFrom.r / 255f)
        byteBuf.writeFloat(rgbFrom.g / 255f)
        byteBuf.writeFloat(rgbFrom.b / 255f)
        byteBuf.writeFloat(scale)
        byteBuf.writeFloat(rgbTo.r / 255f)
        byteBuf.writeFloat(rgbTo.g / 255f)
        byteBuf.writeFloat(rgbTo.b / 255f)
    }
}