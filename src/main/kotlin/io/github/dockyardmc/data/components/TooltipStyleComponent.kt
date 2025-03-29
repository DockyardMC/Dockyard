package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class TooltipStyleComponent(val style: String): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(style)
    }

    companion object: NetworkReadable<TooltipStyleComponent> {
        override fun read(buffer: ByteBuf): TooltipStyleComponent {
            return TooltipStyleComponent()
        }
    }

}