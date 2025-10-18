package io.github.dockyardmc.protocol.packets.configurations.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.tide.stream.StreamCodec

data class ClientboundCodeOfConductPacket(val codeOfConduct: String) : ClientboundPacket() {
    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.STRING, ClientboundCodeOfConductPacket::codeOfConduct,
            ::ClientboundCodeOfConductPacket
        )
    }

    init {
        STREAM_CODEC.write(buffer, this)
    }
}