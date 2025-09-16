package io.github.dockyardmc.codec.transcoder

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.CRC32CHasher.EMPTY
import io.github.dockyardmc.data.Hasher
import io.github.dockyardmc.tide.transcoder.Transcoder

object CRC32CTranscoder : Transcoder<Int> {

    override fun encodeBoolean(value: Boolean): Int {
        return if (value) CRC32CHasher.TRUE else CRC32CHasher.FALSE
    }

    override fun encodeByte(value: Byte): Int {
        return CRC32CHasher.ofByte(value)
    }

    override fun encodeDouble(value: Double): Int {
        return CRC32CHasher.ofDouble(value)
    }

    override fun encodeFloat(value: Float): Int {
        return CRC32CHasher.ofFloat(value)
    }

    override fun encodeInt(value: Int): Int {
        return CRC32CHasher.ofInt(value)
    }

    override fun encodeList(size: Int): Transcoder.ListBuilder<Int> {
        val hasher = Hasher().putByte(CRC32CHasher.TAG_LIST_START)
        return object : Transcoder.ListBuilder<Int> {

            override fun add(value: Int): Transcoder.ListBuilder<Int> {
                hasher.putIntBytes(value)
                return this
            }

            override fun build(): Int {
                return hasher.putByte(CRC32CHasher.TAG_LIST_END).hash()
            }

        }
    }

    override fun encodeLong(value: Long): Int {
        return CRC32CHasher.ofLong(value)
    }

    override fun encodeMap(): Transcoder.VirtualMapBuilder<Int> {
        val map = mutableMapOf<Int, Int>()
        return object : Transcoder.VirtualMapBuilder<Int> {

            override fun put(key: Int, value: Int): Transcoder.VirtualMapBuilder<Int> {
                if (value != EMPTY) {
                    map[key] = value
                }
                return this
            }

            override fun build(): Int {
                if (map.isEmpty()) return CRC32CHasher.EMPTY_MAP
                val hasher = Hasher().putByte(CRC32CHasher.TAG_MAP_START)

                map.entries.stream().sorted(CRC32CHasher.MAP_COMPARATOR).forEach { entry ->
                    hasher.putIntBytes(entry.key)
                    hasher.putIntBytes(entry.value)
                }

                return hasher.putByte(CRC32CHasher.TAG_MAP_END).hash()

            }

            override fun put(key: String, value: Int): Transcoder.VirtualMapBuilder<Int> {
                return put(encodeString(key), value)
            }

        }
    }

    override fun encodeNull(): Int {
        return EMPTY
    }

    override fun encodeShort(value: Short): Int {
        return CRC32CHasher.ofShort(value)
    }

    override fun encodeString(value: String): Int {
        return CRC32CHasher.ofString(value)
    }

    override fun encodeByteArray(value: ByteArray): Int {
        return CRC32CHasher.ofByteArray(value)
    }

    override fun encodeIntArray(value: IntArray): Int {
        return CRC32CHasher.ofIntArray(value)
    }

    override fun encodeLongArray(value: LongArray): Int {
        return CRC32CHasher.ofLongArray(value)
    }

    // only encode, fuck the decoding

    override fun decodeBoolean(value: Int): Boolean {
        throw UnsupportedOperationException()
    }

    override fun decodeByte(value: Int): Byte {
        throw UnsupportedOperationException()
    }

    override fun decodeDouble(value: Int): Double {
        throw UnsupportedOperationException()
    }

    override fun decodeFloat(value: Int): Float {
        throw UnsupportedOperationException()
    }

    override fun decodeInt(value: Int): Int {
        throw UnsupportedOperationException()
    }

    override fun decodeList(value: Int): List<Int> {
        throw UnsupportedOperationException()
    }

    override fun decodeLong(value: Int): Long {
        throw UnsupportedOperationException()
    }

    override fun decodeMap(value: Int): Transcoder.VirtualMap<Int> {
        throw UnsupportedOperationException()
    }

    override fun decodeShort(value: Int): Short {
        throw UnsupportedOperationException()
    }

    override fun decodeString(value: Int): String {
        throw UnsupportedOperationException()
    }
}