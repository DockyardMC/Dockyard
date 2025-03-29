package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class DamageComponent(val damage: Int): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(damage)
    }

    companion object: NetworkReadable<DamageComponent> {
        override fun read(buffer: ByteBuf): DamageComponent {
            return DamageComponent(buffer.readVarInt())
        }
    }
}