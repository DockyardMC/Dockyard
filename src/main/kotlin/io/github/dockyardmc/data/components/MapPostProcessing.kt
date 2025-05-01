package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class MapPostProcessing(val type: Type) : DataComponent() {

    enum class Type {
        LOCK,
        SCALE
    }

    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
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