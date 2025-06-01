package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.writeOptional
import io.netty.buffer.ByteBuf

class UseCooldownComponent(val seconds: Float, val cooldownGroup: String? = null) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(seconds)
        buffer.writeOptional(cooldownGroup, ByteBuf::writeString)
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            static("seconds", CRC32CHasher.ofFloat(seconds))
            optional("cooldown_group", cooldownGroup, CRC32CHasher::ofString)
        }
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