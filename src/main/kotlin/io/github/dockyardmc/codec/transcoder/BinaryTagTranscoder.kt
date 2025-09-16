package io.github.dockyardmc.codec.transcoder

import io.github.dockyardmc.extentions.toByte
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.transcoder.Transcoder
import net.kyori.adventure.nbt.*
import java.util.*

object BinaryTagTranscoder : Transcoder<BinaryTag> {

    override fun decodeBoolean(value: BinaryTag): Boolean {
        return (value as ByteBinaryTag).value() != 0.toByte()
    }

    override fun decodeByte(value: BinaryTag): Byte {
        return (value as ByteBinaryTag).value()
    }

    override fun decodeDouble(value: BinaryTag): Double {
        return (value as DoubleBinaryTag).value()
    }

    override fun decodeFloat(value: BinaryTag): Float {
        return (value as FloatBinaryTag).value()
    }

    override fun decodeInt(value: BinaryTag): Int {
        return (value as IntBinaryTag).value()
    }

    override fun decodeList(value: BinaryTag): List<BinaryTag> {
        if (value !is ListBinaryTag) throw Codec.DecodingException("Not a list ($value)")
        val listTag = value.unwrapHeterogeneity()
        return object : AbstractList<BinaryTag>() {
            override fun get(index: Int): BinaryTag {
                return listTag[index]
            }

            override val size: Int get() = listTag.size()
        }
    }

    override fun decodeLong(value: BinaryTag): Long {
        return (value as LongBinaryTag).value()
    }

    override fun decodeMap(value: BinaryTag): Transcoder.VirtualMap<BinaryTag> {
        if (value !is CompoundBinaryTag) throw Codec.DecodingException("Not a compound ($value)")

        return object : Transcoder.VirtualMap<BinaryTag> {

            override fun getKeys(): Collection<String> {
                return value.keySet()
            }

            override fun getValue(key: String): BinaryTag {
                return value[key] ?: throw Codec.DecodingException("Value with key $key not present in compound tag")
            }

            override fun hasValue(key: String): Boolean {
                return value.get(key) != null
            }
        }
    }

    override fun decodeShort(value: BinaryTag): Short {
        return (value as ShortBinaryTag).value()
    }

    override fun decodeString(value: BinaryTag): String {
        return (value as StringBinaryTag).value()
    }

    override fun encodeBoolean(value: Boolean): BinaryTag {
        return ByteBinaryTag.byteBinaryTag(value.toByte())
    }

    override fun encodeByte(value: Byte): BinaryTag {
        return ByteBinaryTag.byteBinaryTag(value)
    }

    override fun encodeDouble(value: Double): BinaryTag {
        return DoubleBinaryTag.doubleBinaryTag(value)
    }

    override fun encodeFloat(value: Float): BinaryTag {
        return FloatBinaryTag.floatBinaryTag(value)
    }

    override fun encodeInt(value: Int): BinaryTag {
        return IntBinaryTag.intBinaryTag(value)
    }

    override fun encodeList(size: Int): Transcoder.ListBuilder<BinaryTag> {
        val elements = ListBinaryTag.heterogeneousListBinaryTag()
        return object : Transcoder.ListBuilder<BinaryTag> {

            override fun add(value: BinaryTag): Transcoder.ListBuilder<BinaryTag> {
                elements.add(value)
                return this
            }

            override fun build(): BinaryTag {
                return elements.build()
            }
        }
    }

    override fun encodeLong(value: Long): BinaryTag {
        return LongBinaryTag.longBinaryTag(value)
    }

    override fun encodeMap(): Transcoder.VirtualMapBuilder<BinaryTag> {
        val compound = CompoundBinaryTag.builder()

        return object : Transcoder.VirtualMapBuilder<BinaryTag> {

            override fun put(key: BinaryTag, value: BinaryTag): Transcoder.VirtualMapBuilder<BinaryTag> {
                if (value !is EndBinaryTag && key is StringBinaryTag) compound.put(key.value(), value)
                return this
            }

            override fun build(): BinaryTag {
                return compound.build()
            }

            override fun put(key: String, value: BinaryTag): Transcoder.VirtualMapBuilder<BinaryTag> {
                if (value !is EndBinaryTag) compound.put(key, value)
                return this
            }
        }
    }

    override fun encodeNull(): BinaryTag {
        return EndBinaryTag.endBinaryTag()
    }

    override fun encodeShort(value: Short): BinaryTag {
        return ShortBinaryTag.shortBinaryTag(value)
    }

    override fun encodeString(value: String): BinaryTag {
        return StringBinaryTag.stringBinaryTag(value)
    }
}