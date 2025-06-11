package io.github.dockyardmc.data

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.NetworkWritable
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import kotlin.reflect.KClass

// Component list stored as a patch of added and removed components (even if none are removed)
// The inner map contains value for added components, null for removed components and no entry for unmodified components

// heavily inspired by minestom's implementation of the data component patch
class DataComponentPatch(internal val components: Int2ObjectMap<DataComponent?>, val isPatch: Boolean, val isTrusted: Boolean) : NetworkWritable {

    // Not NotworkReadable because need read requires isPatch and isTrusted
    companion object {
        const val REMOVAL_PREFIX: Char = '!'
        val EMPTY = DataComponentPatch(Int2ObjectArrayMap<DataComponent?>(0), false, true)

        fun fromList(components: List<DataComponent>): DataComponentPatch {
            var patch = DataComponentPatch(Int2ObjectArrayMap(), true, true)
            components.forEach { component ->
                patch = patch.set(component)
            }
            return patch
        }

        fun patchNetworkType(components: Int2ObjectMap<DataComponent?>): DataComponentPatch {
            return DataComponentPatch(components, true, true)
        }

        fun untrustedPatchNetworkType(components: Int2ObjectMap<DataComponent?>): DataComponentPatch {
            return DataComponentPatch(components, true, false)
        }

        fun read(buffer: ByteBuf, isPatch: Boolean, isTrusted: Boolean): DataComponentPatch {
            val added = buffer.readVarInt()
            val removed = if (isPatch) buffer.readVarInt() else 0

            if (added + removed > 256) throw IllegalStateException("Data component map too large: ${added + removed} > 256")
            val patch: Int2ObjectMap<DataComponent?> = Int2ObjectArrayMap(added + removed)

            for (i in 0 until added) {
                val id = buffer.readVarInt()
                val componentClass = DataComponentRegistry.dataComponentsById[id] ?: throw IllegalStateException("Unknown component with id $id")
                if (isTrusted) {
                    val component = componentClass.asNetworkReadable<DataComponent>().read(buffer)
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
            return DataComponentPatch(patch, isPatch, isTrusted)
        }
    }

    fun isEmpty(): Boolean = components.isEmpty()

    fun has(component: DataComponent): Boolean {
        val componentId = component.getId()
        return components.containsKey(componentId) && components.get(componentId) != null
    }

    fun has(kclass: KClass<out DataComponent>): Boolean {
        val id = DataComponentRegistry.dataComponentsByIdReversed.getOrThrow(kclass)
        return components.containsKey(id) && components.get(id) != null
    }

    fun has(prototype: DataComponentPatch, component: DataComponent): Boolean {
        val id = component.getId()
        return if (components.containsKey(id)) {
            components.get(id) != null
        } else {
            prototype.has(component)
        }
    }

    operator fun get(component: KClass<out DataComponent>): DataComponent? {
        val key = DataComponentRegistry.dataComponentsByIdReversed.getValue(component)
        if (!components.containsKey(key)) return null
        return components.getValue(key)
    }

    operator fun get(component: DataComponent): DataComponent? {
        return get(component::class)
    }

    inline fun <reified T : DataComponent> get(): T? {
        return get(T::class) as T?
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
        components.put(component.getId(), component)
        return this
    }

    fun remove(component: DataComponent): DataComponentPatch {
        components.put(component.getId(), null)
        return this
    }

    fun remove(componentClass: KClass<out DataComponent>): DataComponentPatch {
        val id = DataComponentRegistry.dataComponentsByIdReversed.getOrThrow(componentClass)
        components.put(id, null)
        return this
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