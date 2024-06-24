package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeStringArray
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scoreboard.Scoreboard

@WikiVGEntry("Reset Score")
@ClientboundPacketInfo(0x44, ProtocolState.PLAY)
class ClientboundResetScorePacket(
    val entity: Entity,
    val scoreboard: Scoreboard?
): ClientboundPacket() {
    constructor(entity: Entity) : this(entity, null)

    init {
        data.writeUtf(entity.usernameOrUUID())
        data.writeBoolean(scoreboard != null)

        if (scoreboard != null) {
            data.writeUtf(scoreboard.name)
        }
    }
}