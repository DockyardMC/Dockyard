package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.writeOptional
import io.netty.buffer.ByteBuf

class CooldownItemComponent(val seconds: Float, val cooldownGroup: String? = null) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(seconds)
        buffer.writeOptional(cooldownGroup, ByteBuf::writeString)
    }

    companion object : NetworkReadable<CooldownItemComponent> {
        override fun read(buffer: ByteBuf): CooldownItemComponent {
            return CooldownItemComponent(
                buffer.readFloat(),
                buffer.readOptional(ByteBuf::readString)
            )
        }
    }
}