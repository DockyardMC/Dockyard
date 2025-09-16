package io.github.dockyardmc.codec

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

object ExtraCodecs {

    object LongArray {
        val STREAM = object : StreamCodec<kotlin.LongArray> {

            override fun write(buffer: ByteBuf, value: kotlin.LongArray) {
                buffer.writeVarInt(value.size)
                value.forEach { buffer.writeLong(it) }
            }

            override fun read(buffer: ByteBuf): kotlin.LongArray {
                val size = buffer.readVarInt()
                val longs = mutableListOf<Long>()
                for (i in 0 until size) {
                    longs.add(buffer.readLong())
                }
                return longs.toLongArray()
            }
        }

        val CODEC = Codec.LONG_ARRAY
    }

    object BitSet {
        val STREAM = LongArray.STREAM.transform<java.util.BitSet>(java.util.BitSet::toLongArray, java.util.BitSet::valueOf)
        val CODEC = LongArray.CODEC.transform<java.util.BitSet>(java.util.BitSet::valueOf, java.util.BitSet::toLongArray)
    }
}