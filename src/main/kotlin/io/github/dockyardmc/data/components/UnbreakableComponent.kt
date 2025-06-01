package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class UnbreakableComponent : DataComponent() {

    override fun write(buffer: ByteBuf) {}

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.EMPTY_MAP)
    }

    companion object : NetworkReadable<UnbreakableComponent> {
        override fun read(buffer: ByteBuf): UnbreakableComponent {
            return UnbreakableComponent()
        }
    }
}