package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class CreativeSlotLockComponent : DataComponent() {

    override fun write(buffer: ByteBuf) {
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.EMPTY)
    }

    companion object : NetworkReadable<CreativeSlotLockComponent> {
        override fun read(buffer: ByteBuf): CreativeSlotLockComponent {
            return CreativeSlotLockComponent()
        }
    }
}