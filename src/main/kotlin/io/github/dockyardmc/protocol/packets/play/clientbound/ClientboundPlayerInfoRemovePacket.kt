package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.writeList
import io.netty.buffer.ByteBuf
import java.util.*

class ClientboundPlayerInfoRemovePacket(uuidss: List<UUID>) : ClientboundPacket() {
    constructor(player: Player) : this(mutableListOf(player).map { it.uuid })
    constructor(uuid: UUID) : this(mutableListOf(uuid))

    init {
        data.writeList(uuidss, ByteBuf::writeUUID)
    }

}