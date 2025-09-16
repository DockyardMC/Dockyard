package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.player.ClientParticleSettings
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.tide.stream.StreamCodec
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

        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.STRING, ClientSettings::locale,
            StreamCodec.BYTE, ClientSettings::viewDistance,
            StreamCodec.enum<ChatMessageType>(), ClientSettings::chatMessageType,
            StreamCodec.BOOLEAN, ClientSettings::chatColors,
            StreamCodec.BYTE, ClientSettings::displayedSkinParts,
            StreamCodec.enum<PlayerHand>(), ClientSettings::mainHand,
            StreamCodec.BOOLEAN, ClientSettings::enableTextFiltering,
            StreamCodec.BOOLEAN, ClientSettings::allowServerListings,
            StreamCodec.enum<ClientParticleSettings>(), ClientSettings::particleSettings,
            ::ClientSettings
        )
    }

    enum class ChatMessageType {
        FULL,
        SYSTEM,
        NONE
    }
}