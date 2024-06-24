package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scoreboard.Entry
import io.github.dockyardmc.scoreboard.NumberFormat
import io.github.dockyardmc.scoreboard.Scoreboard
import io.github.dockyardmc.scroll.Component

@WikiVGEntry("Update Score")
@ClientboundPacketInfo(0x61, ProtocolState.PLAY)
class ClientboundUpdateScorePacket(
    val scoreboard: Scoreboard,
    val entry: Entry
): ClientboundPacket() {
    init {
        data.writeUtf(entry.entity)
        data.writeUtf(scoreboard.name)
        data.writeVarInt(entry.value)
        data.writeBoolean(entry.displayName != null)
        if (entry.displayName != null) data.writeNBT(entry.displayName.toNBT())
        data.writeBoolean(entry.numberFormat != null)
        entry.numberFormat?.write(data)
    }
}