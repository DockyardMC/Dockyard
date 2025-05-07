package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.DyeColor
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

data class SheepColorComponent(val color: DyeColor) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(color)
    }

    companion object : NetworkReadable<SheepColorComponent> {
        override fun read(buffer: ByteBuf): SheepColorComponent {
            return SheepColorComponent(buffer.readEnum())
        }
    }
}