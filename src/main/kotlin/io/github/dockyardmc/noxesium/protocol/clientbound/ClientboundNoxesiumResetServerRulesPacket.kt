package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs

data class ClientboundNoxesiumResetServerRulesPacket(val indices: List<Int>) : NoxesiumPacket {

    override fun getStreamCodec(): Codec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = Codec.of(
            "indices", Codecs.VarInt.list(), ClientboundNoxesiumResetServerRulesPacket::indices,
            ::ClientboundNoxesiumResetServerRulesPacket
        )
    }
}