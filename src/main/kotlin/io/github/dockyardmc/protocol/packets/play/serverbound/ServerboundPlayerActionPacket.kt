package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerBlockBreakEvent
import io.github.dockyardmc.extentions.readByteEnum
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.item.*
import io.github.dockyardmc.location.readBlockPosition
import io.github.dockyardmc.particles.BlockParticleData
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.vectors.Vector3f
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Player Action")
@ServerboundPacketInfo(36, ProtocolState.PLAY)
class ServerboundPlayerActionPacket(
    var action: PlayerAction,
    var position: Vector3,
    var face: Direction,
    var sequence: Int,
) : ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        if (action == PlayerAction.START_DIGGING) {
            if (player.gameMode.value == GameMode.CREATIVE) {

                val previousBlock = player.world.getBlock(position.toLocation(player.world))
                val item = player.getHeldItem(PlayerHand.MAIN_HAND)

                val event = PlayerBlockBreakEvent(player, previousBlock, position.toLocation(player.world))
                Events.dispatch(event)
                if (event.cancelled) {
                    player.world.getChunkAt(position.x, position.z)?.let { player.sendPacket(it.packet) }
                    return
                }

                if (item.material == Items.DEBUG_STICK) {
                    player.world.getChunkAt(position.x, position.z)?.let { player.sendPacket(it.packet) }
                    return
                }

                player.world.setBlock(event.location, Blocks.AIR)
                player.world.players.values.filter { it != player }.spawnParticle(
                    event.location.getBlockLocation(),
                    Particles.BLOCK,
                    amount = 50,
                    offset = Vector3f(0.3f),
                    particleData = BlockParticleData(previousBlock)
                )
            }
        }

        if (action == PlayerAction.HELD_ITEM_UPDATE) {

            //TODO Add multi hand support
            val item = player.getHeldItem(PlayerHand.MAIN_HAND)
            val isFood = item.components.hasType(FoodItemComponent::class)
            if (isFood) {
                player.itemInUse = null
            }
        }

        if (action == PlayerAction.DROP_ITEM) {
            val held = player.getHeldItem(PlayerHand.MAIN_HAND)
            if (held.isEmpty()) return
            player.inventory.drop(held, isEntireStack = false, isHeld = true)
        }

        if (action == PlayerAction.DROP_ITEM_STACK) {
            val held = player.getHeldItem(PlayerHand.MAIN_HAND)
            if (held.isEmpty()) return
            player.inventory.drop(held, isEntireStack = true, isHeld = true)
        }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerActionPacket =
            ServerboundPlayerActionPacket(
                buf.readVarIntEnum<PlayerAction>(),
                buf.readBlockPosition(),
                buf.readByteEnum<Direction>(),
                buf.readVarInt()
            )
    }
}

enum class PlayerAction {
    START_DIGGING,
    CANCELLED_DIGGING,
    FINISHED_DIGGING,
    DROP_ITEM_STACK,
    DROP_ITEM,
    HELD_ITEM_UPDATE,
    SWAP_ITEM
}