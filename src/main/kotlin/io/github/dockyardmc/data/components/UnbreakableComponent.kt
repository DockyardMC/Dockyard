package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class UnbreakableComponent: DataComponent() {

    override fun write(buffer: ByteBuf) {}

    companion object: NetworkReadable<UnbreakableComponent> {
        override fun read(buffer: ByteBuf): UnbreakableComponent {
            return UnbreakableComponent()
        }
    }
}