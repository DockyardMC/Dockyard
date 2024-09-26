package io.github.dockyardmc.particles

import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.scroll.CustomColor
import io.netty.buffer.ByteBuf

class DustTransitionParticleData(val fromColor: CustomColor, val toColor: CustomColor, val scale: Float = 1f): ParticleData {

    constructor(hexFrom: String, hexTo: String, scale: Float = 1f): this(CustomColor.fromHex(hexFrom), CustomColor.fromHex(hexTo), scale)

    override var id: Int = Particles.DUST_COLOR_TRANSITION.protocolId

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeFloat(fromColor.r / 255f)
        byteBuf.writeFloat(fromColor.g / 255f)
        byteBuf.writeFloat(fromColor.b / 255f)
        byteBuf.writeFloat(scale)
        byteBuf.writeFloat(toColor.r / 255f)
        byteBuf.writeFloat(toColor.g / 255f)
        byteBuf.writeFloat(toColor.b / 255f)
    }
}