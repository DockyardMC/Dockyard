package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

data class ClientboundMoveVehiclePacket(val position: Vector3) : ClientboundPacket() {

    init {
        STREAM_CODEC.write(buffer, this)
    }

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            Vector3.STREAM_CODEC, ClientboundMoveVehiclePacket::position,
            ::ClientboundMoveVehiclePacket
        )

        fun read(buffer: ByteBuf): ClientboundMoveVehiclePacket {
            return STREAM_CODEC.read(buffer)
        }
    }
}