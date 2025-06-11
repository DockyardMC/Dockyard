package io.github.dockyardmc.item

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.DataComponentRegistry
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.readMap
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.protocol.types.writeMap
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.netty.buffer.ByteBuf
import kotlin.reflect.KClass

class HashedItemStack(val item: Item, val amount: Int, val addedComponents: Map<KClass<out DataComponent>, Int>, val removedComponents: Set<KClass<out DataComponent>>) : NetworkWritable {

    override fun write(buffer: ByteBuf) {

        val resolvedAdded = addedComponents.mapKeys { entry -> DataComponentRegistry.dataComponentsByIdReversed.getOrThrow(entry.key) }
        val resolvedRemoved = removedComponents.map { entry -> DataComponentRegistry.dataComponentsByIdReversed.getOrThrow(entry) }

        buffer.writeBoolean(true)
        buffer.writeRegistryEntry(item)
        buffer.writeVarInt(amount)
        buffer.writeMap(resolvedAdded, ByteBuf::writeVarInt, ByteBuf::writeInt)
        buffer.writeList(resolvedRemoved, ByteBuf::writeVarInt)
    }

    companion object : NetworkReadable<HashedItemStack> {

        val AIR = HashedItemStack(Items.AIR, 0, mapOf(), setOf())

        override fun read(buffer: ByteBuf): HashedItemStack {
            if (!buffer.readBoolean()) return AIR
            val item = buffer.readRegistryEntry<Item>(ItemRegistry)
            val amount = buffer.readVarInt()

            val addedComponents = mutableMapOf<KClass<out DataComponent>, Int>()
            val removedComponents = mutableSetOf<KClass<out DataComponent>>()

            val addedResolved = buffer.readMap(ByteBuf::readVarInt, ByteBuf::readInt)
            val removedResolved = buffer.readList(ByteBuf::readVarInt)

            addedResolved.forEach { (id, hash) ->
                val component = DataComponentRegistry.dataComponentsById.getOrThrow(id)
                addedComponents[component] = hash
            }

            removedComponents.addAll(removedResolved.map { id -> DataComponentRegistry.dataComponentsById.getOrThrow(id) })

            return HashedItemStack(item, amount, addedComponents, removedComponents)
        }


        fun fromItemStack(itemStack: ItemStack): HashedItemStack {

            val addedComponents = mutableMapOf<KClass<out DataComponent>, Int>()
            val removedComponents = mutableSetOf<KClass<out DataComponent>>()

            itemStack.components.components.forEach { (key, value) ->
                if (value != null) {
                    addedComponents[value::class] = value.hashStruct().getHashed()
                } else {
                    removedComponents.add(DataComponentRegistry.dataComponentsById.getValue(key))
                }
            }

            return HashedItemStack(itemStack.material, itemStack.amount, addedComponents, removedComponents)
        }
    }
}