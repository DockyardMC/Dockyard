package io.github.dockyardmc.noxesium.rules

import com.noxcrew.noxesium.api.protocol.rule.ServerRule
import com.noxcrew.noxesium.api.qib.QibDefinition
import io.github.dockyardmc.extentions.write
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.protocol.types.writeMap
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.scroll.CustomColor
import io.netty.buffer.ByteBuf

abstract class NoxesiumServerRule<T : Any?>(val index: Int, val default: T) : ServerRule<T, ByteBuf>() {

    private var value: T = default

    override fun getIndex(): Int {
        return index
    }

    override fun getDefault(): T {
        return default
    }

    override fun getValue(): T {
        return value
    }

    override fun setValue(value: T) {
        this.value = value
    }

    override fun read(buffer: ByteBuf): T = throw UnsupportedOperationException("Cannot read a server-side server rule from a buffer")

    class IntServerRule(index: Int, default: Int = 0) : NoxesiumServerRule<Int>(index, default) {
        override fun write(value: Int, buffer: ByteBuf) {
            buffer.writeVarInt(value)
        }
    }

    class DoubleServerRule(index: Int, default: Double = 0.0) : NoxesiumServerRule<Double>(index, default) {
        override fun write(value: Double, buffer: ByteBuf) {
            buffer.writeDouble(value)
        }
    }

    class StringServerRule(index: Int, default: String) : NoxesiumServerRule<String>(index, default) {
        override fun write(value: String, buffer: ByteBuf) {
            buffer.writeString(value)
        }
    }

    class StringListServerRule(index: Int, default: List<String> = listOf()) : NoxesiumServerRule<List<String>>(index, default) {
        override fun write(value: List<String>, buffer: ByteBuf) {
            buffer.writeList(value, ByteBuf::writeString)
        }
    }

    class ItemStackListServerRule(index: Int, default: List<ItemStack> = listOf()) : NoxesiumServerRule<List<ItemStack>>(index, default) {
        override fun write(value: List<ItemStack>, buffer: ByteBuf) {
            buffer.writeList(value, ItemStack::write)
        }
    }

    class ColorServerRule(index: Int, default: CustomColor? = null) : NoxesiumServerRule<CustomColor?>(index, default) {
        override fun write(value: CustomColor?, buffer: ByteBuf) {
            buffer.writeOptional(value, CustomColor::write)
        }
    }

    class OptionalEnumServerRule<T : Enum<T>>(index: Int, default: T? = null) : NoxesiumServerRule<T?>(index, default) {

        override fun write(value: T?, buffer: ByteBuf) {
            buffer.writeOptional(value?.ordinal, ByteBuf::writeVarInt)
        }
    }

    class QibBehaviourServerRule(index: Int, default: Map<String, QibDefinition> = emptyMap()) : NoxesiumServerRule<Map<String, QibDefinition>>(index, default) {
        override fun write(value: Map<String, QibDefinition>, buffer: ByteBuf) {
            buffer.writeMap(value.mapValues { map -> QibDefinition.QIB_GSON.toJson(map.value) }, ByteBuf::writeString, ByteBuf::writeString)
        }
    }

    class IntListServerRule(index: Int, default: List<Int> = listOf()) : NoxesiumServerRule<List<Int>>(index, default) {
        override fun write(value: List<Int>, buffer: ByteBuf) {
            buffer.writeList(value, ByteBuf::writeInt)
        }
    }

}