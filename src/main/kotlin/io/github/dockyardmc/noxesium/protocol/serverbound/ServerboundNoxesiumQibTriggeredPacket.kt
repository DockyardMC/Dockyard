package io.github.dockyardmc.noxesium.protocol.serverbound

import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs

class ServerboundNoxesiumQibTriggeredPacket(
    val behaviour: String,
    val qibType: Type,
    val entityId: Int,
) : NoxesiumPacket {

    enum class Type {
        JUMP,
        INSIDE,
        ENTER,
        LEAVE,
    }

    override fun getStreamCodec(): Codec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = Codec.of(
            "behaviour", Codecs.String, ServerboundNoxesiumQibTriggeredPacket::behaviour,
            "qib_type", Codec.enum<Type>(), ServerboundNoxesiumQibTriggeredPacket::qibType,
            "entity_id", Codecs.VarInt, ServerboundNoxesiumQibTriggeredPacket::entityId,
            ::ServerboundNoxesiumQibTriggeredPacket
        )
    }
}