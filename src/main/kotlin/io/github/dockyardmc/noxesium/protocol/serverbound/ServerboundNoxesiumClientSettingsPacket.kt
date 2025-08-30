package io.github.dockyardmc.noxesium.protocol.serverbound

import com.noxcrew.noxesium.api.protocol.ClientSettings
import io.github.dockyardmc.noxesium.protocol.NoxesiumCodecs
import io.github.dockyardmc.protocol.plugin.messages.PluginMessage
import io.github.dockyardmc.tide.stream.StreamCodec

data class ServerboundNoxesiumClientSettingsPacket(val clientSettings: ClientSettings) : PluginMessage {

    override fun getStreamCodec(): StreamCodec<out PluginMessage> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            NoxesiumCodecs.CLIENT_SETTINGS, ServerboundNoxesiumClientSettingsPacket::clientSettings,
            ::ServerboundNoxesiumClientSettingsPacket
        )
    }
}