package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.extentions.readByteEnum
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.readBlockPosition
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPlayerActionPacket(
    var action: PlayerAction,
    var position: Vector3,
    var face: Direction,
    var sequence: Int
): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        if(action == PlayerAction.START_DIGGING) {
            if(player.gameMode == GameMode.CREATIVE) {
                DockyardServer.broadcastMessage("$player broke blocc $position (seq $sequence)")
                val chunk = player.world.getChunkAt(position.x, position.z) ?: return
                chunk.setBlock(position.x, position.y, position.z, Blocks.AIR)
                chunk.cacheChunkDataPacket()
                player.sendPacket(chunk.packet)
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