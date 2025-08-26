package io.github.dockyardmc.codec

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Registry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.tide.transcoder.Transcoder
import io.netty.buffer.ByteBuf

object RegistryCodec {


    fun <T : RegistryEntry> stream(registry: Registry<T>): StreamCodec<T> {
        return object : StreamCodec<T> {

            override fun write(buffer: ByteBuf, value: T) {
                buffer.writeVarInt(value.getProtocolId())
            }

            override fun read(buffer: ByteBuf): T {
                return registry.getByProtocolId(buffer.readVarInt())
            }

        }
    }

    fun <T : RegistryEntry> codec(registry: Registry<T>): Codec<T> {
        return object : Codec<T> {

            override fun <D> encode(transcoder: Transcoder<D>, value: T): D {
                return transcoder.encodeString(value.getEntryIdentifier())
            }

            override fun <D> decode(transcoder: Transcoder<D>, value: D): T {
                return registry[transcoder.decodeString(value)]
            }
        }
    }
}
