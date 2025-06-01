package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class MapPostProcessing(val type: Type) : DataComponent() {

    enum class Type {
        LOCK,
        SCALE
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofEnum(type))
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(type)
    }

    companion object : NetworkReadable<MapPostProcessing> {
        override fun read(buffer: ByteBuf): MapPostProcessing {
            return MapPostProcessing(buffer.readEnum())
        }
    }
}