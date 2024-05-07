package io.github.dockyardmc.protocol.packets.play

import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.netty.channel.ChannelHandlerContext

class PlayHandler(var processor: PacketProcessor): PacketHandler(processor) {

    fun handleTeleportConfirmation(packet: ServerboundTeleportConformationPacket, connection: ChannelHandlerContext) {

    }

}