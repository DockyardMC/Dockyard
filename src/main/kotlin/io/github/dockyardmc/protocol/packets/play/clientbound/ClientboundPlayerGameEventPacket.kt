package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Game Event")
@ClientboundPacketInfo(0x22, ProtocolState.PLAY)
class ClientboundPlayerGameEventPacket(
    event: GameEvent,
    value: Float,
): ClientboundPacket() {

    init {
        data.writeByte(event.ordinal)
        data.writeFloat(value)
    }
}

enum class GameEvent {
    NO_RESPAWN_BLOCK_AVAILABLE,
    END_RAINING,
    START_RAINING,
    CHANGE_GAME_MODE,
    WIN_GAME,
    DEMO_EVENT,
    ARROW_HIT_PLAYER,
    RAIN_LEVEL_CHANGE,
    THUNDER_LEVEL_CHANGE,
    PLAY_PUFFERFISH_STING,
    PLAY_ELDER_GUARDIAN_JUMPSCARE,
    ENABLE_RESPAWN_SCREEN,
    LIMITED_CRAFTING,
    START_WAITING_FOR_CHUNKS
}