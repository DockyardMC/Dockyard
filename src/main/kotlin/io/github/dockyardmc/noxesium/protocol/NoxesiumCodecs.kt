package io.github.dockyardmc.noxesium.protocol

import com.google.gson.JsonElement
import com.noxcrew.noxesium.api.protocol.ClientSettings
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Transcoder
import io.netty.buffer.ByteBuf

object NoxesiumCodecs {

    object Writers : Codec<Map<Int, (ByteBuf) -> Unit>> {

        override fun writeNetwork(buffer: ByteBuf, value: Map<Int, (ByteBuf) -> Unit>) {
            val values = value.entries.toList()
            val indices = values.map { it.key }
            buffer.writeList(indices, ByteBuf::writeVarInt)
            values.forEach { entry ->
                entry.value.invoke(buffer)
            }
        }

        override fun readJson(json: JsonElement, field: String): Map<Int, (ByteBuf) -> Unit> {
            throw UnsupportedOperationException()
        }

        override fun readNetwork(buffer: ByteBuf): Map<Int, (ByteBuf) -> Unit> {
            throw UnsupportedOperationException()
        }

        override fun <A> readTranscoded(transcoder: Transcoder<A>, format: A, field: String): Map<Int, (ByteBuf) -> Unit> {
            throw UnsupportedOperationException()
        }

        override fun <A> writeTranscoded(transcoder: Transcoder<A>, format: A, value: Map<Int, (ByteBuf) -> Unit>, field: String) {
            throw UnsupportedOperationException()
        }

        override fun writeJson(json: JsonElement, value: Map<Int, (ByteBuf) -> Unit>, field: String) {
            throw UnsupportedOperationException()
        }

    }

    object ClientSettings : Codec<com.noxcrew.noxesium.api.protocol.ClientSettings> {

        override fun writeNetwork(buffer: ByteBuf, value: com.noxcrew.noxesium.api.protocol.ClientSettings) {
            buffer.writeVarInt(value.configuredGuiScale)
            buffer.writeDouble(value.trueGuiScale)
            buffer.writeVarInt(value.width)
            buffer.writeVarInt(value.height)
            buffer.writeBoolean(value.enforceUnicode)
            buffer.writeBoolean(value.touchScreenMode)
            buffer.writeDouble(value.notificationDisplayTime)
        }

        override fun readJson(json: JsonElement, field: String): com.noxcrew.noxesium.api.protocol.ClientSettings {
            throw UnsupportedOperationException()
        }

        override fun readNetwork(buffer: ByteBuf): com.noxcrew.noxesium.api.protocol.ClientSettings {
            return ClientSettings(
                buffer.readVarInt(),
                buffer.readDouble(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readDouble(),
            )
        }

        override fun <A> readTranscoded(transcoder: Transcoder<A>, format: A, field: String): com.noxcrew.noxesium.api.protocol.ClientSettings {
            throw UnsupportedOperationException()
        }

        override fun <A> writeTranscoded(transcoder: Transcoder<A>, format: A, value: com.noxcrew.noxesium.api.protocol.ClientSettings, field: String) {
            throw UnsupportedOperationException()
        }

        override fun writeJson(json: JsonElement, value: com.noxcrew.noxesium.api.protocol.ClientSettings, field: String) {
            throw UnsupportedOperationException()
        }
    }
}