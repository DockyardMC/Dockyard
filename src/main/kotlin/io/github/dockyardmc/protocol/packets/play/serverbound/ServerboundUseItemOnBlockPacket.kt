package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerBlockPlaceEvent
import io.github.dockyardmc.events.PlayerBlockRightClickEvent
import io.github.dockyardmc.events.PlayerFinishPlacingBlockEvent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.readBlockPosition
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.player.systems.startConsumingIfApplicable
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.BlockRegistry
import io.github.dockyardmc.utils.debug
import io.github.dockyardmc.utils.getPlayerEventContext
import io.github.dockyardmc.utils.isDoubleInteract
import io.github.dockyardmc.world.block.Block
import io.github.dockyardmc.world.block.GeneralBlockPlacementRules
import io.github.dockyardmc.world.block.handlers.BlockHandlerManager
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundUseItemOnBlockPacket(
    var hand: PlayerHand,
    var pos: Vector3,
    var face: Direction,
    var cursorX: Float,
    var cursorY: Float,
    var cursorZ: Float,
    var insideBlock: Boolean,
    var hitWorldBorder: Boolean,
    var sequence: Int,
) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val item = player.getHeldItem(hand)

        // since minecraft sends 2 packets at once, we need to make sure that only one gets handled
        if (isDoubleInteract(player)) return

        var cancelled = false

        val newPos = pos.copy()
        val originalBlock = player.world.getBlock(pos.toLocation(player.world))

        when (face) {
            Direction.UP -> newPos.y += 1
            Direction.DOWN -> newPos.y += -1
            Direction.WEST -> newPos.x += -1
            Direction.SOUTH -> newPos.z += 1
            Direction.EAST -> newPos.x += 1
            Direction.NORTH -> newPos.z += -1
        }

        // prevent desync?
        pos.toLocation(player.world).getChunk()?.let { chunk ->
            player.sendPacket(chunk.packet)
        }

        val event = PlayerBlockRightClickEvent(
            player,
            item,
            player.world.getBlock(pos.toLocation(player.world)),
            face,
            pos.toLocation(player.world)
        )
        Events.dispatch(event)

        if (event.cancelled) cancelled = true

        if (!event.cancelled) startConsumingIfApplicable(item, player)

        var used = false
        BlockHandlerManager.getAllFromRegistryBlock(originalBlock.registryBlock).forEach { handler ->
            used = handler.onUse(player, hand, player.getHeldItem(hand), originalBlock, face, pos.toLocation(player.world), Vector3f(cursorX, cursorY, cursorZ)) || used
        }

        if (used) {
            player.lastInteractionTime = System.currentTimeMillis()
            return
        }

        if ((item.material.isBlock) && (item.material != Items.AIR) && (player.gameMode.value != GameMode.ADVENTURE && player.gameMode.value != GameMode.SPECTATOR)) {
            var block: Block = (BlockRegistry.getOrNull(item.material.identifier) ?: Blocks.AIR).toBlock()

            BlockHandlerManager.getAllFromRegistryBlock(block.registryBlock).forEach { handler ->
                val result = handler.onPlace(player, item, block, face, newPos.toLocation(player.world), pos.toLocation(player.world), Vector3f(cursorX, cursorY, cursorZ))
                if (result == null) {
                    cancelled = true
                    return@forEach
                }

                block = result
            }

            val canBePlaced = GeneralBlockPlacementRules.canBePlaced(
                pos.toLocation(player.world),
                newPos.toLocation(player.world),
                block,
                player
            )
            if (!canBePlaced.canBePlaced) {
                cancelled = true
            }

            val blockPlaceEvent = PlayerBlockPlaceEvent(player, block, newPos.toLocation(player.world))

            Events.dispatch(blockPlaceEvent)

            if (blockPlaceEvent.cancelled) cancelled = true

            if (blockPlaceEvent.location.y <= -64.0) cancelled = true
            if (blockPlaceEvent.location.y >= 320.0) cancelled = true

            val finishPlacingBlockEvent = PlayerFinishPlacingBlockEvent(player, player.world, blockPlaceEvent.block, blockPlaceEvent.location, getPlayerEventContext(player))

            if (cancelled) {
                player.world.getChunkAt(newPos.x, newPos.z)?.let { player.sendPacket(it.packet) }
                player.inventory.sendInventoryUpdate(player.heldSlotIndex.value)
                Events.dispatch(finishPlacingBlockEvent)
                return
            }

            player.world.setBlock(blockPlaceEvent.location, blockPlaceEvent.block)
            blockPlaceEvent.location.getNeighbours().forEach { (_, neighbourLocation) ->
                val handlers = BlockHandlerManager.getAllFromRegistryBlock(neighbourLocation.block.registryBlock)
                handlers.forEach { handler ->
                    handler.onUpdateByNeighbour(neighbourLocation.block, neighbourLocation.world, neighbourLocation, blockPlaceEvent.block, blockPlaceEvent.location)
                }
            }

            if (player.gameMode.value != GameMode.CREATIVE) {
                val heldItem = player.getHeldItem(hand)
                val newItem = if (heldItem.amount <= 1) ItemStack.AIR else heldItem.withAmount(heldItem.amount - 1)
                player.setHeldItem(hand, newItem)
                Events.dispatch(finishPlacingBlockEvent)
            }
        } else {
            // cancelled still equals false
            // just return instead of assigning `true` to `cancelled`
            return
        }

        if(!cancelled) {
            player.lastInteractionTime = System.currentTimeMillis()
        }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundUseItemOnBlockPacket {
            return ServerboundUseItemOnBlockPacket(
                buf.readVarIntEnum<PlayerHand>(),
                buf.readBlockPosition(),
                buf.readVarIntEnum<Direction>(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readVarInt()
            )
        }
    }
}
