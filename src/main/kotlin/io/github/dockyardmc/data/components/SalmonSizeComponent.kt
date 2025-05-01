package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

data class SalmonSizeComponent(val size: Size) : DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(size)
    }

    companion object : NetworkReadable<SalmonSizeComponent> {
        override fun read(buffer: ByteBuf): SalmonSizeComponent {
            return SalmonSizeComponent(buffer.readEnum())
        }
    }

    enum class Size {
        SMALL,
        MEDIUM,
        LARGE
    }
}