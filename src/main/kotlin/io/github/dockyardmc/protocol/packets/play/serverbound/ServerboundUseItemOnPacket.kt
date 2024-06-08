package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.blocks.GeneralBlockPlacementRules
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerBlockInteractEvent
import io.github.dockyardmc.events.PlayerBlockPlaceEvent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.readBlockPosition
import io.github.dockyardmc.utils.toLocation
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundUseItemOnPacket(
    var hand: PlayerHand,
    var pos: Vector3,
    var face: Direction,
    var cursorX: Float,
    var cursorY: Float,
    var cursorZ: Float,
    var insideBlock: Boolean,
    var sequence: Int
    ): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val item = player.getHeldItem(hand)
        player.sendMessage("<gray>Used item <lime>${item.material.name}<gray> on <aqua>$pos")

        val newPos = pos.copy()
        when(face) {
            Direction.UP -> newPos.y += 1
            Direction.DOWN -> newPos.y += -1
            Direction.WEST -> newPos.x += -1
            Direction.SOUTH -> newPos.z += 1
            Direction.EAST -> newPos.x += 1
            Direction.NORTH -> newPos.z += -1
        }

        val event = PlayerBlockInteractEvent(player, item, player.world.getBlock(pos), face, pos.toLocation())
        Events.dispatch(event)

        if(item.material.isBlock && item.material != Items.AIR) {
            val block = Blocks.getBlockById(item.material.blockId!!)
            var cancelled = false

            if(!GeneralBlockPlacementRules.canBePlaced(pos.toLocation(), newPos.toLocation(), block, player)) cancelled = true

            val blockPlaceEvent = PlayerBlockPlaceEvent(player, block, newPos.toLocation())
            Events.dispatch(blockPlaceEvent)
            if(blockPlaceEvent.cancelled) cancelled = true

            if(cancelled) {
                player.world.getChunkAt(newPos.x, newPos.z)?.let { player.sendPacket(it.packet) }
                return
            }

            player.world.setBlock(blockPlaceEvent.location, blockPlaceEvent.block)
        }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundUseItemOnPacket {
            return ServerboundUseItemOnPacket(
                buf.readVarIntEnum<PlayerHand>(),
                buf.readBlockPosition(),
                buf.readVarIntEnum<Direction>(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean(),
                buf.readVarInt()
            )
        }
    }
}