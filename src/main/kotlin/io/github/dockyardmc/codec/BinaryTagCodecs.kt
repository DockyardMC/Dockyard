package io.github.dockyardmc.codec

import io.github.dockyardmc.extentions.readNBT
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.tide.transcoder.Transcoder
import io.netty.buffer.ByteBuf
import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.TagStringIO

object BinaryTagCodecs {

    private val TAG_STRING_IO = TagStringIO.builder().build()

    val STREAM = object : StreamCodec<BinaryTag> {

        override fun write(buffer: ByteBuf, value: BinaryTag) {
            buffer.writeNBT(value)
        }

        override fun read(buffer: ByteBuf): BinaryTag {
            return buffer.readNBT()
        }
    }

    val STRING = object : Codec<BinaryTag> {

        override fun <D> encode(transcoder: Transcoder<D>, value: BinaryTag): D {
            return transcoder.encodeString(TAG_STRING_IO.asString(value))
        }

        override fun <D> decode(transcoder: Transcoder<D>, value: D): BinaryTag {
            return TAG_STRING_IO.asCompound(transcoder.decodeString(value))
        }
    }

}