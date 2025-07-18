package io.github.dockyardmc.particles.data

import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.registries.Particle
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import io.netty.buffer.ByteBuf
import kotlin.time.Duration

class VibrationParticleData(val vibrationSource: VibrationSource, val pos: Vector3, val entityId: Int, val entityEyeHeight: Float, val duration: Duration) : ParticleData {

    override var parentParticle: Particle = Particles.VIBRATION

    enum class VibrationSource {
        BLOCK,
        ENTITY
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum<VibrationSource>(vibrationSource)
        if (vibrationSource == VibrationSource.BLOCK) {
            pos.write(buffer)
            buffer.writeVarInt(duration.inWholeMinecraftTicks)
        } else {
            buffer.writeVarInt(entityId)
            buffer.writeFloat(entityEyeHeight)
            buffer.writeVarInt(duration.inWholeMinecraftTicks)
        }
    }

}

