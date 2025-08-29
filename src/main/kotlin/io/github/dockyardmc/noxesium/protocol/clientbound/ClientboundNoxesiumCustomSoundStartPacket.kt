package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.SoundCategory
import io.github.dockyardmc.tide.stream.StreamCodec

data class ClientboundNoxesiumCustomSoundStartPacket(
    val id: Int,
    val sound: String,
    val category: SoundCategory,
    val looping: Boolean,
    val attenuation: Boolean,
    val ignoreIfPlaying: Boolean,
    val volume: Float,
    val pitch: Float,
    val position: Vector3f,
    val entityId: Int? = null,
    val unix: Long? = null,
    val offset: Float? = null
) : NoxesiumPacket {

    override fun getStreamCodec(): StreamCodec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.VAR_INT, ClientboundNoxesiumCustomSoundStartPacket::id,
            StreamCodec.STRING, ClientboundNoxesiumCustomSoundStartPacket::sound,
            StreamCodec.enum<SoundCategory>(), ClientboundNoxesiumCustomSoundStartPacket::category,
            StreamCodec.BOOLEAN, ClientboundNoxesiumCustomSoundStartPacket::looping,
            StreamCodec.BOOLEAN, ClientboundNoxesiumCustomSoundStartPacket::attenuation,
            StreamCodec.BOOLEAN, ClientboundNoxesiumCustomSoundStartPacket::ignoreIfPlaying,
            StreamCodec.FLOAT, ClientboundNoxesiumCustomSoundStartPacket::volume,
            StreamCodec.FLOAT, ClientboundNoxesiumCustomSoundStartPacket::pitch,
            Vector3f.STREAM_CODEC, ClientboundNoxesiumCustomSoundStartPacket::position,
            StreamCodec.VAR_INT.optional(), ClientboundNoxesiumCustomSoundStartPacket::entityId,
            StreamCodec.LONG.optional(), ClientboundNoxesiumCustomSoundStartPacket::unix,
            StreamCodec.FLOAT.optional(), ClientboundNoxesiumCustomSoundStartPacket::offset,
            ::ClientboundNoxesiumCustomSoundStartPacket
        )
    }
}