package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class UseCooldownComponent(val seconds: Float, val cooldownGroup: String? = null) : DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(seconds)
        buffer.writeOptional(cooldownGroup, ByteBuf::writeString)
    }

    companion object : NetworkReadable<UseCooldownComponent> {
        override fun read(buffer: ByteBuf): UseCooldownComponent {
            return UseCooldownComponent(
                buffer.readFloat(),
                buffer.readOptional(ByteBuf::readString)
            )
        }
    }
}