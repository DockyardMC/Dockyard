package io.github.dockyardmc.particles.data

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.registry.Particles
import io.netty.buffer.ByteBuf

class ItemParticleData(val item: ItemStack): ParticleData {

    override var id: Int = Particles.ITEM.getProtocolId()

    override fun write(buffer: ByteBuf) {
        item.write(buffer)
    }
}