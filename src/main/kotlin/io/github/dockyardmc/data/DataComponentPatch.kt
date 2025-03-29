package io.github.dockyardmc.data

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.NetworkWritable
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet

// Component list stored as a patch of added and removed components (even if none are removed)
// The inner map contains value for added components, null for removed components and no entry for unmodified components

// heavily inspired by minestom's implementation of data component patch
class DataComponentPatch(internal val components: Int2ObjectMap<DataComponent?>, val isPatch: Boolean, val isTrusted: Boolean) : NetworkWritable {

    // Not NotworkReadable because need read requires isPatch and isTrusted
    companion object {
        const val REMOVAL_PREFIX: Char = '!'
        val EMPTY = DataComponentPatch(Int2ObjectArrayMap<DataComponent?>(0), false, true)

        fun patchNetworkType(components: Int2ObjectMap<DataComponent?>): DataComponentPatch {
            return DataComponentPatch(components, true, true)
        }

        fun untrustedPatchNetworkType(components: Int2ObjectMap<DataComponent?>): DataComponentPatch {
            return DataComponentPatch(components, true, false)
        }

        fun read(buffer: ByteBuf, isPatch: Boolean, isTrusted: Boolean) {
            val added = buffer.readVarInt()
            val removed = if(isPatch) buffer.readVarInt() else 0

            if(added + removed > 256) throw IllegalStateException("Data component map too large: ${added + removed} > 256")
            val patch: Int2ObjectMap<DataComponent> = Int2ObjectArrayMap(added + removed)

            for (i in 0 until added) {
                val id = buffer.readVarInt()
                val componentClass = DataComponentRegistry.dataComponentsById[id] ?: throw IllegalStateException("Unknown component with id $id")
                if(isTrusted) {
                    val component = componentClass.read<DataComponent>(buffer)
                    patch.put(id, component)
                } else {
                    // length prefixed byte array consisting of [LENGTH, DATA_COMPONENT]
                    val array = buffer.readByteArray()
                    val tempBuffer = Unpooled.copiedBuffer(array)
                    patch.put(id, componentClass.read<DataComponent>(tempBuffer))
                }
            }
            for (i in 0 until removed) {
                val id = buffer.readVarInt()
                patch.put(id, null)
            }
        }
    }

    fun entrySet(): ObjectSet<Int2ObjectMap.Entry<DataComponent?>>? {
        return components.int2ObjectEntrySet()
    }

    fun isEmpty(): Boolean = components.isEmpty()

    fun has(component: DataComponent): Boolean {
        val componentId = component.getId()
        return components.containsKey(componentId) && components.get(componentId) != null
    }

    fun has(prototype: DataComponentPatch, component: DataComponent): Boolean {
        val id = component.getId()
        return if (components.containsKey(id)) {
            components.get(id) != null
        } else {
            prototype.has(component)
        }
    }

    operator fun get(component: DataComponent): DataComponent? {
        return components.getValue(component.getId())
    }

    fun getOfPrototype(prototype: DataComponentPatch, component: DataComponent): DataComponent? {
        val id = component.getId()
        return if (components.containsKey(id)) {
            components.getValue(id)
        } else {
            prototype[component]
        }
    }

    fun set(component: DataComponent): DataComponentPatch {
        val newComponents: Int2ObjectMap<DataComponent?> = Int2ObjectArrayMap<DataComponent?>(components)
        newComponents.put(component.getId(), component)
        return DataComponentPatch(newComponents, isPatch, isTrusted)
    }

    fun remove(component: DataComponent): DataComponentPatch {
        val newComponents: Int2ObjectMap<DataComponent?> = Int2ObjectArrayMap<DataComponent?>(components)
        newComponents.put(component.getId(), null)
        return DataComponentPatch(newComponents, isPatch, isTrusted)
    }

    override fun write(buffer: ByteBuf) {
        var added = 0
        components.values.forEach { component ->
            if (component != null) added++
        }

        buffer.writeVarInt(added)

        // amount of new components added
        if (isPatch) {
            buffer.writeVarInt(components.size - added)
        }

        // write added
        components.int2ObjectEntrySet().forEach { entry ->
            if (entry.value == null) return@forEach

            buffer.writeVarInt(entry.intKey)

            if (isTrusted) {
                entry.value!!.write(buffer)
            } else {
                // Need to length prefix it, so write to another buffer first then copy.
                val componentData = Buffer.makeArray { b -> entry.value!!.write(b) }
                buffer.writeByteArray(componentData)
            }
        }

        // Write removed data components patch
        if (isPatch) {
            components.int2ObjectEntrySet().forEach { entry ->
                if (entry.value != null) return@forEach

                buffer.writeVarInt(entry.intKey)
            }
        }
    }
}