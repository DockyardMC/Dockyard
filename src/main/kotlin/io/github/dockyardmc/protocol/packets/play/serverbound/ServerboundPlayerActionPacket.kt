package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerBlockBreakEvent
import io.github.dockyardmc.extentions.readByteEnum
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.readBlockPosition
import io.github.dockyardmc.utils.toLocation
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@ServerboundPacketInfo(33, ProtocolState.PLAY)
class ServerboundPlayerActionPacket(
    var action: PlayerAction,
    var position: Vector3,
    var face: Direction,
    var sequence: Int
): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        if(action == PlayerAction.START_DIGGING) {
            if(player.gameMode.value == GameMode.CREATIVE) {

                val previousBlock = player.world.getBlock(position)

                val event = PlayerBlockBreakEvent(player, previousBlock, position.toLocation())
                Events.dispatch(event)
                if(event.cancelled) {
                    player.world.getChunkAt(position.x, position.z)?.let { player.sendPacket(it.packet) }
                    return
                }

                player.world.setBlock(event.location, Blocks.AIR)
            }
        }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerActionPacket {
            return ServerboundPlayerActionPacket(buf.readVarIntEnum<PlayerAction>(), buf.readBlockPosition(), buf.readByteEnum<Direction>(), buf.readVarInt())
        }
    }
}

enum class PlayerAction {
    START_DIGGING,
    CANCELLED_DIGGING,
    DROP_ITEM_STACK,
    DROP_ITEM,
    HELD_ITE_UPDATE,
    SWAP_ITEM
}