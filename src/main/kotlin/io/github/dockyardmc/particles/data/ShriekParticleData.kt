package io.github.dockyardmc.particles.data

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.registries.Particle
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import io.netty.buffer.ByteBuf
import kotlin.time.Duration

class ShriekParticleData(val delay: Duration) : ParticleData {

    override val parentParticle: Particle = Particles.SHRIEK

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(delay.inWholeMinecraftTicks)
    }
}