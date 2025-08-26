package io.github.dockyardmc.codec

import io.github.dockyardmc.extentions.readNBT
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf
import net.kyori.adventure.nbt.BinaryTag

object BinaryTagCodecs {

    val STREAM = object : StreamCodec<BinaryTag> {

        override fun write(buffer: ByteBuf, value: BinaryTag) {
            buffer.writeNBT(value)
        }

        override fun read(buffer: ByteBuf): BinaryTag {
            return buffer.readNBT()
        }
    }

}