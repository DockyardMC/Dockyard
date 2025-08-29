package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

data class MaxStackSizeComponent(val size: Int) : DataComponent(true) {

    override fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, this)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofInt(size))
    }

    companion object : NetworkReadable<MaxStackSizeComponent> {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.VAR_INT, MaxStackSizeComponent::size,
            ::MaxStackSizeComponent
        )

        override fun read(buffer: ByteBuf): MaxStackSizeComponent {
            return STREAM_CODEC.read(buffer)
        }
    }
}