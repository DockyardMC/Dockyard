package io.github.dockyardmc.sailboat

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.WrappedPacket
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.socket.NetworkManager
import io.netty.channel.ChannelHandlerContext

class PlayerNetworkManager: NetworkManager(ServerPacketRegistry, ClientPacketRegistry) {

    override fun getServerCompressionThreshold(): Int {
        return 256
    }

    override fun handlePacket(connection: ChannelHandlerContext, packet: WrappedPacket) {
        val handler = serverPacketRegistry.getHandler<Packet>(packet.packet::class)
        handler.invoke(packet.packet, this, connection)
    }

    override fun kick(message: Component, connection: ChannelHandlerContext) {

    }

}

