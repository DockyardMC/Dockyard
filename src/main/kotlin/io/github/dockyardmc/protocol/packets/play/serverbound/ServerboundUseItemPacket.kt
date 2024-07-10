package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerRightClickWithItemEvent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.item.*
import io.github.dockyardmc.player.ItemInUse
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Use Item")
@ServerboundPacketInfo(0x39, ProtocolState.PLAY)
class ServerboundUseItemPacket(val hand: PlayerHand, val sequence: Int, val yaw: Float, val pitch: Float): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val item = player.getHeldItem(PlayerHand.MAIN_HAND)

        val event = PlayerRightClickWithItemEvent(player, item)
        Events.dispatch(event)
        if(event.cancelled) return

        val isFood = item.components.hasType(FoodItemComponent::class)
        if(isFood) {
            if(player.itemInUse != null) return
            val component = item.components.firstOrNullByType<FoodItemComponent>(FoodItemComponent::class)!!
            player.sendMessage("<lime>start eating ${item.displayName}")
            val eatingTime = component.secondsToEat
            val useTime = (eatingTime * 20f).toInt()
            val startTime = player.world.worldAge
            player.itemInUse = ItemInUse(item, startTime, useTime.toLong())
        }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundUseItemPacket =
            ServerboundUseItemPacket(buf.readVarIntEnum<PlayerHand>(), buf.readVarInt(), buf.readFloat(), buf.readFloat())
    }
}