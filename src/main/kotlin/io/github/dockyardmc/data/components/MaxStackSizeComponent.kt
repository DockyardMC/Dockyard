package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs
import io.netty.buffer.ByteBuf

class MaxStackSizeComponent(val size: Int) : DataComponent(true) {

    override fun write(buffer: ByteBuf) {
        CODEC.writeNetwork(buffer, this)
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