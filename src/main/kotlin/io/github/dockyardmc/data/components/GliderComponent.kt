package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class GliderComponent: DataComponent() {
    
    override fun write(buffer: ByteBuf) {
    }
    
    companion object: NetworkReadable<GliderComponent> {
        override fun read(buffer: ByteBuf): GliderComponent {
            return GliderComponent()
        }
    }
}