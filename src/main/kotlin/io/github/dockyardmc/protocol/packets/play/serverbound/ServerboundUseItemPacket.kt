package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerRightClickWithItemEvent
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.player.systems.startConsumingIfApplicable
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundUseItemPacket(val hand: PlayerHand, val sequence: Int, val yaw: Float, val pitch: Float): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val item = player.getHeldItem(PlayerHand.MAIN_HAND)

        val event = PlayerRightClickWithItemEvent(player, item, getPlayerEventContext(player))
        Events.dispatch(event)
        if(event.cancelled) return

        startConsumingIfApplicable(item, player)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundUseItemPacket =
            ServerboundUseItemPacket(buf.readEnum<PlayerHand>(), buf.readVarInt(), buf.readFloat(), buf.readFloat())
    }
}