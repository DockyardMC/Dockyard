package io.github.dockyardmc.nbt

import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.BinaryTagType
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.ListBinaryTag

class NbtBuilder {
    val compound = CompoundBinaryTag.builder()

    fun withInt(name: String, value: Int): NbtBuilder {
        compound.putInt(name, value)
        return this
    }

    fun withString(name: String, value: String): NbtBuilder {
        compound.putString(name, value)
        return this
    }

    fun withDouble(name: String, value: Double): NbtBuilder {
        compound.putDouble(name, value)
        return this
    }

    fun withFloat(name: String, value: Float): NbtBuilder {
        compound.putFloat(name, value)
        return this
    }

    fun withLong(name: String, value: Long): NbtBuilder {
        compound.putLong(name, value)
        return this
    }

    fun withByte(name: String, value: Byte): NbtBuilder {
        compound.putByte(name, value)
        return this
    }

    fun withByteArray(name: String, value: ByteArray): NbtBuilder {
        compound.putByteArray(name, value)
        return this
    }

    fun withIntArray(name: String, value: IntArray): NbtBuilder {
        compound.putIntArray(name, value)
        return this
    }

    fun withLongArray(name: String, value: LongArray): NbtBuilder {
        compound.putLongArray(name, value)
        return this
    }

    fun withBoolean(name: String, value: Boolean): NbtBuilder {
        compound.putBoolean(name, value)
        return this
    }

    fun withCompound(name: String, value: CompoundBinaryTag): NbtBuilder {
        compound.put(name, value)
        return this
    }

    fun withList(name: String, type: BinaryTagType<*>, list: List<BinaryTag>) {
        compound.put(name, ListBinaryTag.listBinaryTag(type, list))
    }

    fun withCompound(value: CompoundBinaryTag): NbtBuilder {
        compound.put(value)
        return this
    }

    fun withCompound(unit: NbtBuilder.() -> Unit) {
        val builder = NbtBuilder()
        unit.invoke(builder)
        compound.put(builder.build())
    }

    fun withCompound(name: String, unit: NbtBuilder.() -> Unit) {
        val builder = NbtBuilder()
        unit.invoke(builder)
        compound.put(name, builder.build())
    }

    fun build(): CompoundBinaryTag = compound.build()
}

fun nbt(compound: NbtBuilder.() -> Unit): CompoundBinaryTag {
    val builder = NbtBuilder()
    compound.invoke(builder)
    return builder.build()
}
