package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class MaxDamageComponent(val maxDamage: Int): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(maxDamage)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofInt(maxDamage))
    }

    companion object: NetworkReadable<MaxDamageComponent> {
        override fun read(buffer: ByteBuf): MaxDamageComponent {
            return MaxDamageComponent(buffer.readVarInt())
        }
    }
}