package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPlayerInfoUpdatePacket(action: Int, numberOfPlayers: Int, updates: MutableList<PlayerInfoUpdate>): ClientboundPacket(0x3C) {

    init {
        data.writeByte(action)
        data.writeVarInt(numberOfPlayers)

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