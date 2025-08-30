package io.github.dockyardmc.noxesium.protocol.serverbound

import io.github.dockyardmc.protocol.plugin.messages.PluginMessage
import io.github.dockyardmc.tide.stream.StreamCodec

data class ServerboundNoxesiumQibTriggeredPacket(
    val behaviour: String,
    val qibType: Type,
    val entityId: Int,
) : PluginMessage {

    enum class Type {
        JUMP,
        INSIDE,
        ENTER,
        LEAVE,
    }

    override fun getStreamCodec(): StreamCodec<out PluginMessage> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.STRING, ServerboundNoxesiumQibTriggeredPacket::behaviour,
            StreamCodec.enum<Type>(), ServerboundNoxesiumQibTriggeredPacket::qibType,
            StreamCodec.VAR_INT, ServerboundNoxesiumQibTriggeredPacket::entityId,
            ::ServerboundNoxesiumQibTriggeredPacket
        )
    }
}