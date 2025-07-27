package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.noxesium.protocol.NoxesiumCodecs
import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs
import io.netty.buffer.ByteBuf

data class ClientboundNoxesiumSetExtraEntityDataPacket(
    val entityId: Int,
    val writers: Map<Int, (ByteBuf) -> Unit>
) : NoxesiumPacket {

    override fun getStreamCodec(): Codec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = Codec.of(
            "entity_id", Codecs.VarInt, ClientboundNoxesiumSetExtraEntityDataPacket::entityId,
            "writers", NoxesiumCodecs.Writers, ClientboundNoxesiumSetExtraEntityDataPacket::writers,
            ::ClientboundNoxesiumSetExtraEntityDataPacket
        )
    }
}