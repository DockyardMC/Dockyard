package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

data class UseCooldownComponent(val seconds: Float, val cooldownGroup: String? = null) : DataComponent() {

    companion object : NetworkReadable<UseCooldownComponent> {

        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.FLOAT, UseCooldownComponent::seconds,
            StreamCodec.STRING.optional(), UseCooldownComponent::cooldownGroup,
            ::UseCooldownComponent
        )

        val CODEC = StructCodec.of(
            "seconds", Codec.FLOAT, UseCooldownComponent::seconds,
            "cooldown_group", Codec.STRING.optional(), UseCooldownComponent::cooldownGroup,
            ::UseCooldownComponent
        )

        override fun read(buffer: ByteBuf): UseCooldownComponent {
            return STREAM_CODEC.read(buffer)
        }
    }

    override fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, this)
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            static("seconds", CRC32CHasher.ofFloat(seconds))
            optional("cooldown_group", cooldownGroup, CRC32CHasher::ofString)
        }
    }
}