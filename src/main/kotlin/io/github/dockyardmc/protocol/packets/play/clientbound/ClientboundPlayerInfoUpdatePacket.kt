package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.addIfNotPresent
import io.github.dockyardmc.extentions.writeByte
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.player.PlayerInfoUpdate
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.types.rawList
import io.github.dockyardmc.protocol.types.writeMap
import io.netty.buffer.ByteBuf
import java.util.*
import kotlin.experimental.or

data class ClientboundPlayerInfoUpdatePacket(val actions: Map<UUID, List<PlayerInfoUpdate>>) : ClientboundPacket() {

    init {
        val typesPresent: MutableSet<PlayerInfoUpdate.Type> = mutableSetOf()
        actions.forEach { (_, updates) ->
            updates.forEach { update ->
                typesPresent.addIfNotPresent(update.type)
            }
        }
        var bitMask: Byte = 0
        typesPresent.sortedBy { it.ordinal }.forEach { type -> bitMask = bitMask or type.mask.toByte() }

        buffer.writeByte(bitMask)
        buffer.writeMap(actions, ByteBuf::writeUUID) { buf, value -> buf.rawList(value, PlayerInfoUpdate::write) }
    }
}