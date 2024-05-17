package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerSwingHandEvent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.player.SwingAnimationHand
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPlayerSwingHandPacket(val hand: SwingAnimationHand): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        Events.dispatch(PlayerSwingHandEvent(processor.player, hand))
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPlayerSwingHandPacket {
            return ServerboundPlayerSwingHandPacket(buf.readEnum<SwingAnimationHand>())
        }
    }
}