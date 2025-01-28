package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.protocol.NetworkWritable
import io.netty.buffer.ByteBuf

interface ItemComponent: NetworkWritable {

    companion object {
        fun read(buffer: ByteBuf): ItemComponent {
            //TODO return ItemComponent()
        }
    }

}