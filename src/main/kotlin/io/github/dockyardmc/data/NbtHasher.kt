package io.github.dockyardmc.data

import net.kyori.adventure.nbt.*

object NbtHasher {

    fun hashTag(tag: BinaryTag): Int {
        return when (tag) {
            is IntBinaryTag -> CRC32CHasher.ofInt(tag.value())
            is StringBinaryTag -> CRC32CHasher.ofString(tag.value())
            is ByteBinaryTag -> CRC32CHasher.ofByte(tag.value())
            is DoubleBinaryTag -> CRC32CHasher.ofDouble(tag.value())
            is FloatBinaryTag -> CRC32CHasher.ofFloat(tag.value())
            is ShortBinaryTag -> CRC32CHasher.ofShort(tag.value())
            is LongBinaryTag -> CRC32CHasher.ofLong(tag.value())
            is ByteArrayBinaryTag -> CRC32CHasher.ofByteArray(tag.value())
            is IntArrayBinaryTag -> CRC32CHasher.ofIntArray(tag.value())
            is LongArrayBinaryTag -> CRC32CHasher.ofLongArray(tag.value())
            is ListBinaryTag -> CRC32CHasher.ofList(tag.map { binaryTag -> hashTag(binaryTag) })
            is CompoundBinaryTag -> hashCompound(tag)
            else -> throw IllegalArgumentException("Binary tag ${tag::class.java.simpleName} doesn't have hashing implemented")
        }
    }

    fun hashCompound(compoundBinaryTag: CompoundBinaryTag): Int {
        val map = mutableMapOf<Int, Int>()
        compoundBinaryTag.forEach { tag ->
            map[CRC32CHasher.ofString(tag.key)] = hashTag(tag.value)
        }
        return CRC32CHasher.ofMap(map)
    }
}