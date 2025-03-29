package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class DamageResistantComponent(val tagKey: String): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(tagKey)
    }

    companion object: NetworkReadable<DamageResistantComponent> {
        override fun read(buffer: ByteBuf): DamageResistantComponent {
            return DamageResistantComponent(buffer.readString())
        }
    }
}