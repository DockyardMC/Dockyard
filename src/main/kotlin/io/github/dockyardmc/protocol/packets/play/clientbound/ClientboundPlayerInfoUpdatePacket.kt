package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Player Info Update")
@ClientboundPacketInfo(0x3E, ProtocolState.PLAY)
class ClientboundPlayerInfoUpdatePacket(
    action: Int,
    updates: MutableList<PlayerInfoUpdate>,
): ClientboundPacket() {

    init {
        data.writeByte(action)

        data.writeVarInt(updates.size)
        updates.forEach {
            data.writeUUID(it.uuid)
            when(it.action::class) {
                AddPlayerInfoUpdateAction::class -> {
                    val addAction = it.action as AddPlayerInfoUpdateAction
                    data.writeProfileProperties(addAction.profileProperty)
                }

                UpdateGamemodeInfoUpdateAction::class -> {
                    val updateAction = it.action as UpdateGamemodeInfoUpdateAction
                    data.writeVarInt(updateAction.gameMode.ordinal)
                }

                UpdateListedInfoUpdateAction::class -> {
                    val updateAction = it.action as UpdateListedInfoUpdateAction
                    data.writeBoolean(updateAction.listed)
                }

                UpdateLatencyInfoUpdateAction::class -> {
                    val updateAction = it.action as UpdateLatencyInfoUpdateAction
                    data.writeVarInt(updateAction.latency)
                }

                UpdateDisplayNameInfoUpdateAction::class -> {
                    val updateAction = it.action as UpdateDisplayNameInfoUpdateAction
                    data.writeBoolean(updateAction.hasDisplayName)
                    data.writeNBT(updateAction.displayName.toNBT())
                }
            }
        }
    }
}