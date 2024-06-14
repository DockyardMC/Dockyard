package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.PlayerAction
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

// Note: Do not confuse with io.github.dockyardmc.commands packets, this is packet that
// describes actions of player (if they are sneaking, sprinting etc.)
// idk why they named it "player command" packet, im just following the standard
@WikiVGEntry("Player Command")
@ServerboundPacketInfo(37, ProtocolState.PLAY)
class ServerboundPlayerCommandPacket(val entityId: Int, val action: PlayerAction): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = PlayerManager.playerToEntityIdMap[entityId] ?: return

            val event = when(action) {
                PlayerAction.SNEAKING_START -> {
                    player.isSneaking = true
                    player.pose.value = EntityPose.SNEAKING
                    PlayerSneakToggleEvent(player, true)
                }
                PlayerAction.SNEAKING_STOP -> {
                    player.isSneaking = false
                    player.pose.value = EntityPose.STANDING
                    PlayerSneakToggleEvent(player, true)
                }
                PlayerAction.LEAVE_BED -> PlayerBedLeaveEvent(player)
                PlayerAction.SPRINTING_START -> { player.isSprinting = true; PlayerSprintToggleEvent(player, true) }
                PlayerAction.SPRINTING_END -> { player.isSprinting = false; PlayerSprintToggleEvent(player, false) }
                PlayerAction.HORSE_JUMP_START -> HorseJumpEvent(player, true)
                PlayerAction.HORSE_JUMP_END -> HorseJumpEvent(player, true)
                PlayerAction.VEHICLE_INVENTORY_OPEN -> PlayerVehicleInventoryOpenEvent(player)
                PlayerAction.ELYTRA_FLYING_START -> PlayerElytraFlyingStartEvent(player)
            }

        Events.dispatch(event)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerCommandPacket {
            return ServerboundPlayerCommandPacket(buf.readVarInt(), buf.readVarIntEnum<PlayerAction>())
        }
    }
}