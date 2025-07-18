package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

data class SalmonSizeComponent(val size: Size) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(size)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofEnum(size))
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