package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.PlayerAction
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

// Note: Do not confuse with io.github.dockyardmc.commands packets, this is packet that
// describes actions of player (if they are sneaking, sprinting etc.)
// idk why they named it "player command" packet, im just following the standard
class ServerboundPlayerCommandPacket(val entityId: Int, val action: PlayerAction) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player

        val event = when (action) {
            PlayerAction.SNEAKING_START -> {
                player.isSneaking = true

                // the only pose that allows sneaking
                if (player.pose.value == EntityPose.STANDING &&
                    !player.isFlying.value
                ) {
                    player.pose.value = EntityPose.SNEAKING
                }

                player.dismountCurrentVehicle()

                PlayerSneakToggleEvent(player, true)

            }

            PlayerAction.SNEAKING_STOP -> {
                player.isSneaking = false

                if (player.pose.value == EntityPose.SNEAKING) {
                    player.pose.value = EntityPose.STANDING
                }

                PlayerSneakToggleEvent(player, true)
            }

            PlayerAction.LEAVE_BED -> PlayerBedLeaveEvent(player)
            PlayerAction.SPRINTING_START -> {
                player.isSprinting = true; PlayerSprintToggleEvent(player, true)
            }

            PlayerAction.SPRINTING_END -> {
                player.isSprinting = false; PlayerSprintToggleEvent(player, false)
            }

            PlayerAction.HORSE_JUMP_START -> HorseJumpEvent(player, true)
            PlayerAction.HORSE_JUMP_END -> HorseJumpEvent(player, true)
            PlayerAction.VEHICLE_INVENTORY_OPEN -> PlayerVehicleInventoryOpenEvent(player)
            PlayerAction.ELYTRA_FLYING_START -> PlayerElytraFlyingStartEvent(player)
        }

        Events.dispatch(event)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerCommandPacket {
            val entityId = buf.readVarInt()
            val action = buf.readVarIntEnum<PlayerAction>()
            val jumpBoost = buf.readVarInt()
            return ServerboundPlayerCommandPacket(entityId, action)
        }
    }
}
