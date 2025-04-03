package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class MapIdComponent(val mapId: Int): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(mapId)
    }

    companion object: NetworkReadable<MapIdComponent> {
        override fun read(buffer: ByteBuf): MapIdComponent {
            return MapIdComponent(buffer.readVarInt())
        }
    }
}