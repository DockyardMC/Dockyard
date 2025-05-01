package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.fromRGBInt
import io.github.dockyardmc.extentions.toRgbInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class DyedColorComponent(val color: CustomColor) : DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeInt(color.toRgbInt())
    }

    companion object : NetworkReadable<DyedColorComponent> {
        override fun read(buffer: ByteBuf): DyedColorComponent {
            return DyedColorComponent(CustomColor.fromRGBInt(buffer.readInt()))
        }
    }

}