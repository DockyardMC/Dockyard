package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class TooltipStyleComponent(val style: String) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(style)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofString(style))
    }

    companion object : NetworkReadable<TooltipStyleComponent> {
        override fun read(buffer: ByteBuf): TooltipStyleComponent {
            return TooltipStyleComponent(buffer.readString())
        }
    }

}