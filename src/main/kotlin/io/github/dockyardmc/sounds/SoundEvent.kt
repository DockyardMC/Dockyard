package io.github.dockyardmc.sounds

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.registry.registries.SoundRegistry
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.tide.transcoder.Transcoder
import io.netty.buffer.ByteBuf

sealed interface SoundEvent : DataComponentHashable {

    val identifier: String

    companion object {
        val STREAM_CODEC = object : StreamCodec<SoundEvent> {

            @Suppress("UNCHECKED_CAST")
            override fun write(buffer: ByteBuf, value: SoundEvent) {
                when (value) {
                    is BuiltinSoundEvent -> buffer.writeVarInt(value.protocolId + 1)
                    is CustomSoundEvent -> {
                        buffer.writeVarInt(0)
                        buffer.writeString(value.identifier)
                        buffer.writeOptional(value.range, ByteBuf::writeFloat)
                    }
                }
            }

            override fun read(buffer: ByteBuf): SoundEvent {
                val id = buffer.readVarInt() - 1
                if (id != -1) return BuiltinSoundEvent(id)

                return CustomSoundEvent(buffer.readString(), buffer.readOptional(ByteBuf::readFloat))
            }
        }

        val CODEC = object : Codec<SoundEvent> {

            override fun <D> encode(transcoder: Transcoder<D>, value: SoundEvent): D {
                return when (value) {
                    is BuiltinSoundEvent -> transcoder.encodeString(value.identifier)
                    is CustomSoundEvent -> CustomSoundEvent.CODEC.encode(transcoder, value)
                }
            }

            override fun <D> decode(transcoder: Transcoder<D>, value: D): SoundEvent {
                val result = runCatching { transcoder.decodeString(value) }
                if (result.isSuccess) return BuiltinSoundEvent(result.getOrThrow())

                return CustomSoundEvent.CODEC.decode(transcoder, value)
            }
        }
    }
}

data class BuiltinSoundEvent(override val identifier: String) : SoundEvent {

    constructor(id: Int) : this(SoundRegistry.getByProtocolId(id))

    val protocolId = SoundRegistry[identifier]

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofString(identifier))
    }
}

data class CustomSoundEvent(override val identifier: String, val range: Float? = null) : SoundEvent {

    companion object {
        val CODEC = StructCodec.of(
            "sound_id", Codec.STRING, CustomSoundEvent::identifier,
            "range", Codec.FLOAT.optional(), CustomSoundEvent::range,
            ::CustomSoundEvent
        )
    }

//    override fun write(buffer: ByteBuf) {
//        buffer.writeVarInt(0)
//        buffer.writeString(identifier)
//        buffer.writeOptional<Float>(range, ByteBuf::writeFloat)
//    }


    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            static("sound_id", CRC32CHasher.ofString(identifier))
            optional("range", range, CRC32CHasher::ofFloat)
        }
    }
}