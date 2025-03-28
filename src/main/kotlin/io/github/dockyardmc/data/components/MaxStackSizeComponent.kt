package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class MaxStackSizeComponent(val size: Int): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(size)
    }

    companion object: NetworkReadable<MaxStackSizeComponent> {
        override fun read(buffer: ByteBuf): MaxStackSizeComponent {
            return MaxStackSizeComponent(buffer.readVarInt())
        }
    }
}