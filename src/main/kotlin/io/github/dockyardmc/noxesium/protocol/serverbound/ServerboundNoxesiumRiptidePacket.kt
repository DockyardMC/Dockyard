package io.github.dockyardmc.noxesium.protocol.serverbound

import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs

data class ServerboundNoxesiumRiptidePacket(
    val slot: Int
) : NoxesiumPacket {

    override fun getStreamCodec(): Codec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = Codec.of(
            "slot", Codecs.VarInt, ServerboundNoxesiumRiptidePacket::slot,
            ::ServerboundNoxesiumRiptidePacket
        )
    }
}