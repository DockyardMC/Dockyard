package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs
import io.netty.buffer.ByteBuf

class MaxStackSizeComponent(val size: Int) : DataComponent(true) {

    override fun write(buffer: ByteBuf) {
        CODEC.writeNetwork(buffer, this)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofInt(size))
    }

    companion object : NetworkReadable<MaxStackSizeComponent> {
        val CODEC = Codec.of(
            "size", Codecs.VarInt, MaxStackSizeComponent::size,
            ::MaxStackSizeComponent
        )

        override fun read(buffer: ByteBuf): MaxStackSizeComponent {
            return CODEC.readNetwork(buffer)
        }
    }
}