package io.github.dockyardmc.item

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.DataComponentRegistry
import io.github.dockyardmc.data.Hasher
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.registry.registries.Item
import io.netty.buffer.ByteBuf
import kotlin.reflect.KClass

class HashedItemStack(val item: Item, val amount: Int, val addedComponents: Map<KClass<out DataComponent>, Int>, val removedComponents: Set<KClass<out DataComponent>>) : NetworkWritable {

    override fun write(buffer: ByteBuf) {

    }

    companion object {
        fun fromItemStack(itemStack: ItemStack): HashedItemStack {

            val addedComponents = mutableMapOf<KClass<out DataComponent>, Int>()
            val removedComponents = mutableSetOf<KClass<out DataComponent>>()
            val hasher = Hasher()

            itemStack.components.components.forEach { (key, value) ->
                if (value != null) {
                    addedComponents[value::class] = key //HASH
                } else {
                    removedComponents.add(DataComponentRegistry.dataComponentsById.getValue(key))
                }
            }

            return HashedItemStack(itemStack.material, itemStack.amount, addedComponents, removedComponents)
        }
    }
}