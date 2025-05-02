package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class CreativeSlotLockComponent(): DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
    }

    companion object: NetworkReadable<CreativeSlotLockComponent> {
        override fun read(buffer: ByteBuf): CreativeSlotLockComponent {
            return CreativeSlotLockComponent()
        }
    }
}