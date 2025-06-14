package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.asRGBHash
import io.github.dockyardmc.extentions.fromRGBInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.scroll.CustomColor
import io.netty.buffer.ByteBuf

class DyedColorComponent(val color: CustomColor) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeInt(color.asRGBHash())
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofColor(color))
    }

    companion object : NetworkReadable<DyedColorComponent> {
        override fun read(buffer: ByteBuf): DyedColorComponent {
            return DyedColorComponent(CustomColor.fromRGBInt(buffer.readInt()))
        }
    }

}