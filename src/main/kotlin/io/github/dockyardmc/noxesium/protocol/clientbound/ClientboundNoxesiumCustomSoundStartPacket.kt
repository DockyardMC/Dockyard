package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.SoundCategory
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs

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

    override fun getStreamCodec(): Codec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = Codec.of(
            "id", Codecs.VarInt, ClientboundNoxesiumCustomSoundStartPacket::id,
            "sound", Codecs.String, ClientboundNoxesiumCustomSoundStartPacket::sound,
            "category", Codec.enum<SoundCategory>(), ClientboundNoxesiumCustomSoundStartPacket::category,
            "looping", Codecs.Boolean, ClientboundNoxesiumCustomSoundStartPacket::looping,
            "attenuation", Codecs.Boolean, ClientboundNoxesiumCustomSoundStartPacket::attenuation,
            "ignore_if_playing", Codecs.Boolean, ClientboundNoxesiumCustomSoundStartPacket::ignoreIfPlaying,
            "volume", Codecs.Float, ClientboundNoxesiumCustomSoundStartPacket::volume,
            "pitch", Codecs.Float, ClientboundNoxesiumCustomSoundStartPacket::pitch,
            "position", Vector3f.STREAM_CODEC, ClientboundNoxesiumCustomSoundStartPacket::position,
            "entity_id", Codecs.VarInt.optional(), ClientboundNoxesiumCustomSoundStartPacket::entityId,
            "unix", Codecs.Long.optional(), ClientboundNoxesiumCustomSoundStartPacket::unix,
            "offset", Codecs.Float.optional(), ClientboundNoxesiumCustomSoundStartPacket::offset,
            ::ClientboundNoxesiumCustomSoundStartPacket
        )
    }
}