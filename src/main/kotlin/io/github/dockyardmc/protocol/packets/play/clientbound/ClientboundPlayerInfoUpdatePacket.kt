package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeOptionalOLD
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.extensions.toComponent
import kotlin.experimental.or

class ClientboundPlayerInfoUpdatePacket(vararg updates: PlayerInfoUpdate) : ClientboundPacket() {

    init {
        //TODO Figure out why this wont send with multiple update actions
        var bitMask: Byte = 0
        updates.forEach { bitMask = bitMask or it.action.bitMask }

        data.writeByte(bitMask.toInt())
        data.writeVarInt(1)
        data.writeUUID(updates[0].uuid)
        updates.forEach {
            when (val updateAction = it.action) {
                is AddPlayerInfoUpdateAction -> data.writeProfileProperties(updateAction.profileProperty)
                is UpdateGamemodeInfoUpdateAction -> data.writeVarInt(updateAction.gameMode.ordinal)
                is SetListedInfoUpdateAction -> data.writeBoolean(updateAction.listed)
                is UpdateLatencyInfoUpdateAction -> data.writeVarInt(updateAction.ping)
                is SetDisplayNameInfoUpdateAction -> {
                    data.writeOptionalOLD(updateAction.displayName) { optional ->
                        optional.writeNBT(updateAction.displayName!!.toComponent().toNBT())
                    }
                }
            }
        }
    }
}