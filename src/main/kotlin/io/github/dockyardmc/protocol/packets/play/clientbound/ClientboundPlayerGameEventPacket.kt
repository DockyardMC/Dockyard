package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundPlayerGamceEventPacket(event: GameEvent, value: Float): ClientboundPacket(0x22, ProtocolState.PLAY) {

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