package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.player.PlayerInfoUpdate
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.types.writeRawList
import io.github.dockyardmc.protocol.types.writeMap
import io.netty.buffer.ByteBuf
import java.util.*

data class ClientboundPlayerInfoUpdatePacket(val actions: Map<UUID, List<PlayerInfoUpdate>>) : ClientboundPacket() {

    init {
        // this is bitmask.
        actions
            .asIterable()
            .map { action ->
                action.value
                    .fold(0) { mask, update ->
                        mask or update.type.mask
                    }
            }
            .reduce { l, r ->
                require(l == r) { "mismatched update length. all lists need to have same type bit mask and length" }
                l
            }
            .let(buffer::writeByte)

        buffer.writeMap(actions, ByteBuf::writeUUID) { buf, value ->
            buf.writeRawList(
                value.sortedBy { it.type.ordinal },
                PlayerInfoUpdate::write
            )
        }
    }
}