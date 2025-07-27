package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.player.ClientParticleSettings
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs
import io.github.dockyardmc.world.PlayerChunkViewSystem

data class ClientSettings(
    val locale: String,
    val viewDistance: Byte,
    val chatMessageType: ChatMessageType,
    val chatColors: Boolean,
    val displayedSkinParts: Byte,
    val mainHand: PlayerHand,
    val enableTextFiltering: Boolean,
    val allowServerListings: Boolean,
    val particleSettings: ClientParticleSettings
) {

    companion object {
        val DEFAULT = ClientSettings(
            "en_us",
            PlayerChunkViewSystem.DEFAULT_RENDER_DISTANCE.toByte(),
            ChatMessageType.FULL,
            true,
            0x7F,
            PlayerHand.MAIN_HAND,
            true,
            true,
            ClientParticleSettings.ALL
        )

        val STREAM_CODEC = Codec.of(
            "locale", Codecs.String, ClientSettings::locale,
            "view_distance", Codecs.Byte, ClientSettings::viewDistance,
            "chat_message_type", Codec.enum<ChatMessageType>(), ClientSettings::chatMessageType,
            "chat_colors", Codecs.Boolean, ClientSettings::chatColors,
            "displayed_skin_parts", Codecs.Byte, ClientSettings::displayedSkinParts,
            "main_hand", Codec.enum<PlayerHand>(), ClientSettings::mainHand,
            "enable_text_filtering", Codecs.Boolean, ClientSettings::enableTextFiltering,
            "allow_server_listings", Codecs.Boolean, ClientSettings::allowServerListings,
            "particle_settings", Codec.enum<ClientParticleSettings>(), ClientSettings::particleSettings,
            ::ClientSettings
        )
    }

    enum class ChatMessageType {
        FULL,
        SYSTEM,
        NONE
    }
}