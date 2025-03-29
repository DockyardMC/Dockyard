package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class CreativeSlotLockComponent(): DataComponent() {

    override fun write(buffer: ByteBuf) {
    }

    companion object: NetworkReadable<CreativeSlotLockComponent> {
        override fun read(buffer: ByteBuf): CreativeSlotLockComponent {
            return CreativeSlotLockComponent()
        }
    }
}