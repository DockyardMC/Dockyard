package io.github.dockyardmc.noxesium.protocol

import com.noxcrew.noxesium.api.protocol.ClientSettings
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

object NoxesiumCodecs {


    val WRITERS = object : StreamCodec<Map<Int, (ByteBuf) -> Unit>> {

        override fun write(buffer: ByteBuf, value: Map<Int, (ByteBuf) -> Unit>) {
            val values = value.entries.toList()
            val indices = values.map { it.key }
            buffer.writeList(indices, ByteBuf::writeVarInt)
            values.forEach { entry ->
                entry.value.invoke(buffer)
            }
        }

        override fun read(buffer: ByteBuf): Map<Int, (ByteBuf) -> Unit> {
            throw UnsupportedOperationException()
        }

    }

    val CLIENT_SETTINGS = object : StreamCodec<ClientSettings> {
        override fun write(buffer: ByteBuf, value: ClientSettings) {
            buffer.writeVarInt(value.configuredGuiScale)
            buffer.writeDouble(value.trueGuiScale)
            buffer.writeVarInt(value.width)
            buffer.writeVarInt(value.height)
            buffer.writeBoolean(value.enforceUnicode)
            buffer.writeBoolean(value.touchScreenMode)
            buffer.writeDouble(value.notificationDisplayTime)
        }

        override fun read(buffer: ByteBuf): ClientSettings {
            val result1 = buffer.readVarInt()
            val result2 = buffer.readDouble()
            val result3 = buffer.readVarInt()
            val result4 = buffer.readVarInt()
            val result5 = buffer.readBoolean()
            val result6 = buffer.readBoolean()
            val result7 = buffer.readDouble()
            return ClientSettings(result1, result2, result3, result4, result5, result6, result7)
        }
    }
}