package io.github.dockyardmc.protocol

import io.github.dockyardmc.protocol.packets.ProtocolState
import io.netty.channel.ChannelHandlerContext

open class ExperimentalServerboundPacket(val serverboundPacketId: Int, val serverboundPacketState: ProtocolState) {

    open fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {}

}