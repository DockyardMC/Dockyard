package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.ConsumeEffect
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class DeathProtectionComponent(val deathEffects: List<ConsumeEffect>) : DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeList(deathEffects, ConsumeEffect::write)
    }

    companion object : NetworkReadable<DeathProtectionComponent> {
        override fun read(buffer: ByteBuf): DeathProtectionComponent {
            return DeathProtectionComponent(buffer.readList(ConsumeEffect::read))
        }
    }
}