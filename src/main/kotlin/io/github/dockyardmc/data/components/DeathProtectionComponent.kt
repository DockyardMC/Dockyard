package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.ConsumeEffect
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf

class DeathProtectionComponent(val deathEffects: List<ConsumeEffect>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(emptyList(), ConsumeEffect::write)
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            static("on_consume_effects", CRC32CHasher.EMPTY_LIST) //TODO(1.21.5): Consume effects hashing
        }
    }

    companion object : NetworkReadable<DeathProtectionComponent> {
        override fun read(buffer: ByteBuf): DeathProtectionComponent {
//            return DeathProtectionComponent(buffer.readList(ConsumeEffect::read))
            return DeathProtectionComponent(emptyList())
        }
    }
}