package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scoreboard.Display
import io.github.dockyardmc.scoreboard.Scoreboard

@WikiVGEntry("Display Objective")
@ClientboundPacketInfo(0x57, ProtocolState.PLAY)
class ClientboundDisplayObjectivePacket(
    val scoreboard: Scoreboard,
    val display: Display,
): ClientboundPacket() {
    init {
        data.writeVarInt(display.ordinal)
        data.writeUtf(scoreboard.name)
    }
}