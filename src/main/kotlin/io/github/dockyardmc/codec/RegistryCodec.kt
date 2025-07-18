package io.github.dockyardmc.codec

import com.google.gson.JsonElement
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Registry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Transcoder
import io.github.dockyardmc.tide.asObjectOrThrow
import io.github.dockyardmc.tide.getPrimitive
import io.netty.buffer.ByteBuf

object RegistryCodec {

    class HashType<T: RegistryEntry>(val registry: Registry<*>) : Codec<T> {

        override fun writeNetwork(buffer: ByteBuf, value: T) {
            buffer.writeString(value.getEntryIdentifier())
        }

        override fun readJson(json: JsonElement, field: String): T {
            return registry[json.getPrimitive<String>(field)] as T
        }

        override fun readNetwork(buffer: ByteBuf): T {
            return registry[buffer.readString()] as T
        }

        override fun <A> readTranscoded(transcoder: Transcoder<A>, format: A, field: String): T {
            return registry[transcoder.readString(format, field)] as T
        }

        override fun <A> writeTranscoded(transcoder: Transcoder<A>, format: A, value: T, field: String) {
            transcoder.writeString(format, field, value.getEntryIdentifier())
        }

        override fun writeJson(json: JsonElement, value: T, field: String) {
            json.asObjectOrThrow().addProperty(field, value.getEntryIdentifier())
        }

    }

    class NetworkType<T: RegistryEntry>(val registry: Registry<*>) : Codec<T> {

        override fun writeNetwork(buffer: ByteBuf, value: T) {
            buffer.writeVarInt(value.getProtocolId())
        }

        override fun readJson(json: JsonElement, field: String): T {
            return registry.getByProtocolId(json.getPrimitive<Int>(field)) as T
        }

        override fun readNetwork(buffer: ByteBuf): T {
            return registry.getByProtocolId(buffer.readVarInt()) as T
        }

        override fun <A> readTranscoded(transcoder: Transcoder<A>, format: A, field: String): T {
            return registry.getByProtocolId(transcoder.readVarInt(format, field)) as T
        }

        override fun <A> writeTranscoded(transcoder: Transcoder<A>, format: A, value: T, field: String) {
            transcoder.writeVarInt(format, field, value.getProtocolId())
        }

        override fun writeJson(json: JsonElement, value: T, field: String) {
            json.asObjectOrThrow().addProperty(field, value.getProtocolId())
        }
    }

}

val Codec.Companion.INLINE: String get() = "\$\$inline\$\$"