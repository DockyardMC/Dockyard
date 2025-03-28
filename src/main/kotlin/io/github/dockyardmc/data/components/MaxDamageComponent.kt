package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class MaxDamageComponent(val maxDamage: Int): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(maxDamage)
    }

    companion object: NetworkReadable<MaxDamageComponent> {
        override fun read(buffer: ByteBuf): MaxDamageComponent {
            return MaxDamageComponent(buffer.readVarInt())
        }
    }
}