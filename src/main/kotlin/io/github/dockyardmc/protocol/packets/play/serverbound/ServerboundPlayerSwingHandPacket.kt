package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerSwingHandEvent
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.SwingAnimationHand
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityAnimation
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityAnimation
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPlayerSwingHandPacket(val hand: SwingAnimationHand): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        Events.dispatch(PlayerSwingHandEvent(processor.player, hand))
        val animation = if(hand == SwingAnimationHand.MAIN_HAND) EntityAnimation.SWING_MAIN_ARM else EntityAnimation.SWING_OFFHAND
        val packet = ClientboundEntityAnimation(processor.player, animation)
        processor.player.viewers.forEach { it.connection.sendPacket(packet) }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerSwingHandPacket {
            return ServerboundPlayerSwingHandPacket(buf.readVarIntEnum<SwingAnimationHand>())
        }
    }
}