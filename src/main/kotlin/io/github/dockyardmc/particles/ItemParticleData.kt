package io.github.dockyardmc.particles

import io.github.dockyardmc.inventory.ItemStack
import io.github.dockyardmc.inventory.writeItemStack
import io.github.dockyardmc.registry.Particles
import io.netty.buffer.ByteBuf

class ItemParticleData(val item: ItemStack): ParticleData {

    override var id: Int = Particles.ITEM.id

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeItemStack(item)
    }
}