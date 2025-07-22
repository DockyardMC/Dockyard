package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs

data class ClientboundNoxesiumResetExtraEntityDataPacket(
    val entityId: Int,
    val indices: List<Int>,
) : NoxesiumPacket {

    override fun getStreamCodec(): Codec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = Codec.of(
            "entity_id", Codecs.VarInt, ClientboundNoxesiumResetExtraEntityDataPacket::entityId,
            "indices", Codecs.VarInt.list(), ClientboundNoxesiumResetExtraEntityDataPacket::indices,
            ::ClientboundNoxesiumResetExtraEntityDataPacket
        )
    }

}