package io.github.dockyardmc.noxesium.protocol.serverbound

import com.noxcrew.noxesium.api.protocol.ClientSettings
import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.noxesium.protocol.NoxesiumCodecs
import io.github.dockyardmc.tide.Codec

data class ServerboundNoxesiumClientSettingsPacket(val clientSettings: ClientSettings) : NoxesiumPacket {

    override fun getStreamCodec(): Codec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = Codec.of(
            "client_settings", NoxesiumCodecs.ClientSettings, ServerboundNoxesiumClientSettingsPacket::clientSettings,
            ::ServerboundNoxesiumClientSettingsPacket
        )
    }
}