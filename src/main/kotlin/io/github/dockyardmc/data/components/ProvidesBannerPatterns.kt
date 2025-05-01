package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class ProvidesBannerPatterns(val identifier: String) : DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeString(identifier)
    }

    companion object : NetworkReadable<ProvidesBannerPatterns> {
        override fun read(buffer: ByteBuf): ProvidesBannerPatterns {
            return ProvidesBannerPatterns(buffer.readString())
        }
    }
}