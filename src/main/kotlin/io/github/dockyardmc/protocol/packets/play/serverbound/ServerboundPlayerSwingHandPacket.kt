package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerSwingHandEvent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerAnimationPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityAnimation
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPlayerSwingHandPacket(val hand: PlayerHand) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        Events.dispatch(PlayerSwingHandEvent(player, hand, getPlayerEventContext(player)))
        val animation = if (hand == PlayerHand.MAIN_HAND) EntityAnimation.SWING_MAIN_ARM else EntityAnimation.SWING_OFFHAND
        val packet = ClientboundPlayerAnimationPacket(player, animation)
        player.viewers.forEach { it.sendPacket(packet) }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerSwingHandPacket {
            return ServerboundPlayerSwingHandPacket(buf.readEnum<PlayerHand>())
        }
    }
}