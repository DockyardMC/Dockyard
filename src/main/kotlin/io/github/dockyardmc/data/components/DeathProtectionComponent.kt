package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readConsumeEffects
import io.github.dockyardmc.extentions.writeConsumeEffects
import io.github.dockyardmc.item.ConsumeEffect
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class DeathProtectionComponent(val deathEffects: List<ConsumeEffect>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeConsumeEffects(deathEffects)
    }

    companion object : NetworkReadable<DeathProtectionComponent> {
        override fun read(buffer: ByteBuf): DeathProtectionComponent {
            return DeathProtectionComponent(buffer.readConsumeEffects())
        }
    }
}