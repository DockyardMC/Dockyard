package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class EnchantableComponent(val level: Int): DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(level)
    }

    companion object: NetworkReadable<EnchantableComponent> {
        override fun read(buffer: ByteBuf): EnchantableComponent {
            return EnchantableComponent(buffer.readVarInt())
        }
    }
}