package io.github.dockyardmc.particles.data

import io.github.dockyardmc.extentions.writeColor
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import io.github.dockyardmc.scroll.CustomColor
import io.netty.buffer.ByteBuf
import kotlin.time.Duration

class TrailParticleData(val target: Vector3d, val color: CustomColor, val duration: Duration) : ParticleData {

    override var id: Int = Particles.TRAIL.getProtocolId()

    override fun write(buffer: ByteBuf) {
        target.write(buffer)
        buffer.writeColor(color)
        buffer.writeVarInt(duration.inWholeMinecraftTicks)
    }
}