package io.github.dockyardmc.noxesium.protocol.serverbound

import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs

data class ServerboundNoxesiumClientInformationPacket(
    val protocolVersion: Int,
    val versionString: String,
) : NoxesiumPacket {

    override fun getStreamCodec(): Codec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = Codec.of(
            "protocol_version", Codecs.VarInt, ServerboundNoxesiumClientInformationPacket::protocolVersion,
            "version_string", Codecs.String, ServerboundNoxesiumClientInformationPacket::versionString,
            ::ServerboundNoxesiumClientInformationPacket
        )
    }
}