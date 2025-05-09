package io.github.dockyardmc.particles.data

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import io.netty.buffer.ByteBuf
import kotlin.time.Duration

class ShriekParticleData(val delay: Duration) : ParticleData {

    override var id: Int = Particles.SHRIEK.getProtocolId()

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(delay.inWholeMinecraftTicks)
    }
}