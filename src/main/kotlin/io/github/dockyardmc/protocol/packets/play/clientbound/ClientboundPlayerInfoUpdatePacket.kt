package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scroll.extensions.toComponent

@WikiVGEntry("Player Info Update")
@ClientboundPacketInfo(0x3E, ProtocolState.PLAY)
class ClientboundPlayerInfoUpdatePacket(vararg updates: PlayerInfoUpdate): ClientboundPacket() {

    init {
        //TODO Figure out why this wont send with multiple update actions
        var bitMask = 0
        updates.forEach { bitMask += it.action.bitMask }
        data.writeByte(bitMask)
        data.writeVarInt(updates.size)
        updates.forEach {
            data.writeUUID(it.uuid)
            when(val updateAction = it.action) {
                is AddPlayerInfoUpdateAction -> data.writeProfileProperties(updateAction.profileProperty)
                is UpdateGamemodeInfoUpdateAction -> data.writeVarInt(updateAction.gameMode.ordinal)
                is SetListedInfoUpdateAction -> data.writeBoolean(updateAction.listed)
                is UpdateLatencyInfoUpdateAction -> data.writeVarInt(updateAction.ping)
                is SetDisplayNameInfoUpdateAction -> {
                    data.writeBoolean(updateAction.hasDisplayName)
                    if(updateAction.hasDisplayName) data.writeNBT(updateAction.displayName!!.toComponent().toNBT())
                }
            }
        }
    }
}