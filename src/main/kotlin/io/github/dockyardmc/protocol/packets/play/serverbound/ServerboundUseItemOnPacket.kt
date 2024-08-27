package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.blocks.*
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerBlockRightClickEvent
import io.github.dockyardmc.events.PlayerBlockPlaceEvent
import io.github.dockyardmc.events.PlayerRightClickWithItemEvent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.readPosition
import io.github.dockyardmc.utils.toLocation
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

val placementRules = mutableListOf<BlockPlacementRule>()

@WikiVGEntry("Use Item On")
@ServerboundPacketInfo(56, ProtocolState.PLAY)
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

        init {
            if(ConfigManager.currentConfig.implementationConfig.applyBlockPlacementRules) {
                val rotational = listOf(
                    "furnace",
                    "blast_furnace",
                    "smoker",
                    "chiseled_bookshelf",
                    "beehive",
                    "bee_nest",
                    "observer",
                    "end_portal_frame",
                    "campfire",
                )

                placementRules.add(LogBlockPlacementRules())
                placementRules.add(SlabBlockPlacementRule())
                placementRules.add(StairBlockPlacementRules())
                placementRules.add(WoodBlockPlacementRules())
                placementRules.add(GlassPanePlacementRules())
                placementRules.add(FencePlacementRules())
                placementRules.add(WallPlacementRules())
                placementRules.add(StemBlockPlacementRules())
                placementRules.add(HyphaeBlockPlacementRules())
                placementRules.add(TrapdoorBlockPlacementRule())
                placementRules.add(ButtonBlockPlacementRule())
                placementRules.add(LanternPlacementRules())
                placementRules.add(TorchBlockPlacementRules())
                placementRules.add(RotationPlacementRules(rotational))
            }
        }

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val item = player.getHeldItem(hand)

        var cancelled = false

        val newPos = pos.copy()
        val originalBlock = player.world.getBlock(pos)

        when(face) {
            Direction.UP -> newPos.y += 1
            Direction.DOWN -> newPos.y += -1
            Direction.WEST -> newPos.x += -1
            Direction.SOUTH -> newPos.z += 1
            Direction.EAST -> newPos.x += 1
            Direction.NORTH -> newPos.z += -1
        }

        val event = PlayerBlockRightClickEvent(player, item, player.world.getBlock(pos), face, pos.toLocation(player.world))
        if(event.cancelled) cancelled = true
        Events.dispatch(event)

        val rightClickEvent = PlayerRightClickWithItemEvent(player, item)
        Events.dispatch(event)
        if(rightClickEvent.cancelled) cancelled = true

        //TODO Move to block implementation or something idk?
        if(!player.isSneaking && originalBlock.identifier.contains("trapdoor") && !cancelled) {
            val open = originalBlock.blockStates["open"] != "true"
            player.world.setBlockState(pos.toLocation(player.world), "open" to open.toString().lowercase())
        }

        if(item.material.isBlock && item.material != Items.AIR) {
            var block: Block = Blocks.getBlockById(item.material.blockId!!)

            placementRules.forEach {
                if(block.identifier.contains(it.matchesIdentifier)) {
                    val res = it.getPlacement(player, item, block, face, newPos.toLocation(player.world), pos.toLocation(player.world), cursorX, cursorY, cursorZ)
                    if(res == null) {
                        player.world.getChunkAt(newPos.x, newPos.z)?.let { chunk -> player.sendPacket(chunk.packet) }
                        return
                    }
                    block = res
                }
            }

            if(!GeneralBlockPlacementRules.canBePlaced(pos.toLocation(player.world), newPos.toLocation(player.world), block, player)) cancelled = true

            val blockPlaceEvent = PlayerBlockPlaceEvent(player, block, newPos.toLocation(player.world))

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
                buf.readPosition(),
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