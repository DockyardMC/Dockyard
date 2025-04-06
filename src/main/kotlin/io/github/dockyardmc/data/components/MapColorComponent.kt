package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.fromRGBInt
import io.github.dockyardmc.extentions.toRgbInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.scroll.CustomColor
import io.netty.buffer.ByteBuf

class MapColorComponent(val color: CustomColor) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeInt(color.toRgbInt())
    }

    companion object : NetworkReadable<MapColorComponent> {
        override fun read(buffer: ByteBuf): MapColorComponent {
            return MapColorComponent(CustomColor.fromRGBInt(buffer.readInt()))
        }
    }

}