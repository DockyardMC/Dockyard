package io.github.dockyardmc.particles

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.maths.vectors.Vector3
import io.netty.buffer.ByteBuf

class VibrationParticleData(val vibrationSource: VibrationSource, val pos: Vector3, val entityId: Int, val entityEyeHeight: Float, val ticks: Int): ParticleData {

    override var id: Int = Particles.VIBRATION.getProtocolId()

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeEnum<VibrationSource>(vibrationSource)
        if(vibrationSource == VibrationSource.BLOCK) {
            pos.write(byteBuf)
            byteBuf.writeVarInt(ticks)
        } else {
            byteBuf.writeVarInt(entityId)
            byteBuf.writeFloat(entityEyeHeight)
            byteBuf.writeVarInt(ticks)
        }
    }

}

enum class VibrationSource {
    BLOCK,
    ENTITY
}