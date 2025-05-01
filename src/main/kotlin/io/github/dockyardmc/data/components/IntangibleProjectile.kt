package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class IntangibleProjectile: DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
    }

    companion object: NetworkReadable<IntangibleProjectile> {
        override fun read(buffer: ByteBuf): IntangibleProjectile {
            return IntangibleProjectile()
        }
    }
}