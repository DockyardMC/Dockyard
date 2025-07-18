package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.DyeColor
import io.netty.buffer.ByteBuf

data class WolfCollarComponent(val color: DyeColor) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(color)
    }

    companion object : NetworkReadable<WolfCollarComponent> {
        override fun read(buffer: ByteBuf): WolfCollarComponent {
            return WolfCollarComponent(buffer.readEnum())
        }
    }
}