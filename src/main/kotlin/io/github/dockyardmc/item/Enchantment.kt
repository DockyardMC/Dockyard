package io.github.dockyardmc.item

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.netty.buffer.ByteBuf

class Enchantment(val id: Int, val strength: Int): NetworkWritable {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(id)
        buffer.writeVarInt(strength)
    }


    companion object: NetworkReadable<Enchantment> {
        override fun read(buffer: ByteBuf): Enchantment {
            return Enchantment(buffer.readVarInt(), buffer.readVarInt())
        }
    }
}