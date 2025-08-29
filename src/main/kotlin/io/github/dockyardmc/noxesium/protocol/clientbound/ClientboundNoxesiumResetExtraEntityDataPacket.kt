package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.tide.stream.StreamCodec

data class ClientboundNoxesiumResetExtraEntityDataPacket(
    val entityId: Int,
    val indices: List<Int>,
) : NoxesiumPacket {

    override fun getStreamCodec(): StreamCodec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.VAR_INT, ClientboundNoxesiumResetExtraEntityDataPacket::entityId,
            StreamCodec.VAR_INT.list(), ClientboundNoxesiumResetExtraEntityDataPacket::indices,
            ::ClientboundNoxesiumResetExtraEntityDataPacket
        )
    }

}