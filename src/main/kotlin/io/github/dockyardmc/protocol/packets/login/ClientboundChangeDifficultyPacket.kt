package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.world.Difficulty

class ClientboundChangeDifficultyPacket(difficulty: Difficulty, locked: Boolean = false): ClientboundPacket(11, ProtocolState.LOGIN) {

    init {
        data.writeByte(difficulty.ordinal)
        data.writeBoolean(locked)
    }
}