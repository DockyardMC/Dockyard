package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class TooltipStyleComponent(val style: String): DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeString(style)
    }

    companion object: NetworkReadable<TooltipStyleComponent> {
        override fun read(buffer: ByteBuf): TooltipStyleComponent {
            return TooltipStyleComponent(buffer.readString())
        }
    }

}