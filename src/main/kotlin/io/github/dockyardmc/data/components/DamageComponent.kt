package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs
import io.netty.buffer.ByteBuf

class DamageComponent(val damage: Int) : DataComponent(true) {

    override fun getHashCodec(): Codec<out DataComponent> {
        return CODEC
    }

    override fun write(buffer: ByteBuf) {
        CODEC.writeNetwork(buffer, this)
    }

    companion object : NetworkReadable<DamageComponent> {
        val CODEC = Codec.of(
            "damage", Codecs.VarInt, DamageComponent::damage,
            ::DamageComponent
        )

        override fun read(buffer: ByteBuf): DamageComponent {
            return CODEC.readNetwork(buffer)
        }
    }
}