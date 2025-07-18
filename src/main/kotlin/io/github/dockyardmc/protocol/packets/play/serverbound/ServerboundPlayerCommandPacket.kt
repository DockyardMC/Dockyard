package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.player.PlayerAction
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

// Note: Do not confuse with io.github.dockyardmc.commands packets, this is a packet that
// describes actions of player (if they are sneaking, sprinting etc.)
// idk why they named it "player command" packet, im just following the standard
class ServerboundPlayerCommandPacket(val entityId: Int, val action: PlayerAction) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player

        val event = when (action) {
            PlayerAction.LEAVE_BED -> PlayerBedLeaveEvent(player)
            PlayerAction.SPRINTING_START -> {
                player.isSprinting = true; PlayerSprintToggleEvent(player, true)
            }

            PlayerAction.SPRINTING_END -> {
                player.isSprinting = false; PlayerSprintToggleEvent(player, false)
            }

            PlayerAction.HORSE_JUMP_START -> HorseJumpEvent(player, true)
            PlayerAction.HORSE_JUMP_END -> HorseJumpEvent(player, false)
            PlayerAction.VEHICLE_INVENTORY_OPEN -> PlayerVehicleInventoryOpenEvent(player)
            PlayerAction.ELYTRA_FLYING_START -> PlayerElytraFlyingStartEvent(player)
        }

        Events.dispatch(event)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerCommandPacket {
            val entityId = buf.readVarInt()
            val action = buf.readEnum<PlayerAction>()
            val jumpBoost = buf.readVarInt()
            return ServerboundPlayerCommandPacket(entityId, action)
        }
    }
}
