package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class MapIdComponent(val mapId: Int) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(mapId)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofInt(mapId))
    }

    companion object : NetworkReadable<MapIdComponent> {
        override fun read(buffer: ByteBuf): MapIdComponent {
            return MapIdComponent(buffer.readVarInt())
        }
    }
}