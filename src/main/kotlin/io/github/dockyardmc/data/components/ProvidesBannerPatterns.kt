package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class ProvidesBannerPatterns(val identifier: String) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(identifier)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofString(identifier))
    }

    companion object : NetworkReadable<ProvidesBannerPatterns> {
        override fun read(buffer: ByteBuf): ProvidesBannerPatterns {
            return ProvidesBannerPatterns(buffer.readString())
        }
    }
}