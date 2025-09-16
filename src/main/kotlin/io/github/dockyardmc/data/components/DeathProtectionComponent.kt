package io.github.dockyardmc.data.components

import io.github.dockyardmc.codec.transcoder.CRC32CTranscoder
import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.ConsumeEffect
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

data class DeathProtectionComponent(val deathEffects: List<ConsumeEffect>) : DataComponent() {

    companion object : NetworkReadable<DeathProtectionComponent> {

        val CODEC = StructCodec.of(
            "death_effects", ConsumeEffect.CODEC.list().default(listOf()), DeathProtectionComponent::deathEffects,
            ::DeathProtectionComponent
        )

        val STREAM_CODEC = StreamCodec.of(
            ConsumeEffect.STREAM_CODEC.list(), DeathProtectionComponent::deathEffects,
            ::DeathProtectionComponent
        )

        override fun read(buffer: ByteBuf): DeathProtectionComponent {
            return STREAM_CODEC.read(buffer)
        }
    }

    override fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, this)
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            return StaticHash(CODEC.encode(CRC32CTranscoder, this@DeathProtectionComponent))
        }
    }
}