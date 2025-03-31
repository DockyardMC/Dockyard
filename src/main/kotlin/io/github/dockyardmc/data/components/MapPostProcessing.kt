package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.extentions.writeVarIntEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class MapPostProcessing(val type: Type) : DataComponent() {

    enum class Type {
        LOCK,
        SCALE
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeVarIntEnum(type)
    }

    companion object : NetworkReadable<MapPostProcessing> {
        override fun read(buffer: ByteBuf): MapPostProcessing {
            return MapPostProcessing(buffer.readVarIntEnum())
        }
    }
}