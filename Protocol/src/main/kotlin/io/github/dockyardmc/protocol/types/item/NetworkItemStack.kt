package io.github.dockyardmc.protocol.types.item

import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.registry.registries.Item
import io.github.dockyardmc.protocol.types.ItemComponent
import io.github.dockyardmc.protocol.writers.writeVarInt
import io.netty.buffer.ByteBuf

abstract class NetworkItemStack(
    val material: Item,
    val amount: Int,
    val components: Set<ItemComponent> = setOf(),
    //TODO attributes
): NetworkWritable {

    override fun write(buffer: ByteBuf) {
        if(this.material.identifier == "minecraft:air") {
            buffer.writeVarInt(0)
            return
        }

        val itemComponents = mutableListOf<ItemComponent>()
        itemComponents.addAll(this.components)

        buffer.writeVarInt(this.amount)
        buffer.writeVarInt(this.material.getProtocolId())
        buffer.writeVarInt(itemComponents.size)
        buffer.writeVarInt(0)

        itemComponents.forEach { component ->
            component.write(buffer)
        }
    }

    companion object {
        fun read(buffer: ByteBuf): NetworkItemStack {

        }
    }

}