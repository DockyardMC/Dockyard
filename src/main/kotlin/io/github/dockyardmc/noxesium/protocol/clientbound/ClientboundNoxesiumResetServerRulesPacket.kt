package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.tide.stream.StreamCodec

data class ClientboundNoxesiumResetServerRulesPacket(val indices: List<Int>) : NoxesiumPacket {

    override fun getStreamCodec(): StreamCodec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.VAR_INT.list(), ClientboundNoxesiumResetServerRulesPacket::indices,
            ::ClientboundNoxesiumResetServerRulesPacket
        )
    }
}