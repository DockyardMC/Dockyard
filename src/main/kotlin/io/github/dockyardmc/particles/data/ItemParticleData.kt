package io.github.dockyardmc.particles.data

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.registries.Particle
import io.netty.buffer.ByteBuf

class ItemParticleData(val item: ItemStack): ParticleData {

    override val parentParticle: Particle = Particles.ITEM

    override fun write(buffer: ByteBuf) {
        item.write(buffer)
    }
}