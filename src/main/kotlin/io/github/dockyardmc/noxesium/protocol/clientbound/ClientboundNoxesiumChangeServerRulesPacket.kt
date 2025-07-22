package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.noxesium.protocol.NoxesiumCodecs
import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

data class ClientboundNoxesiumChangeServerRulesPacket(
    val writers: Map<Int, (ByteBuf) -> Unit>
) : NoxesiumPacket {

    override fun getStreamCodec(): Codec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = Codec.of(
            "writers", NoxesiumCodecs.Writers, ClientboundNoxesiumChangeServerRulesPacket::writers,
            ::ClientboundNoxesiumChangeServerRulesPacket
        )
    }
}