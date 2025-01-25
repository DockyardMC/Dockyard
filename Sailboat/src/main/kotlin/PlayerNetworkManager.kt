package io.github.dockyardmc.sailboat

import io.github.dockyardmc.protocol.packets.handshake.serverbound.ServerboundHandshakePacket
import io.github.dockyardmc.socket.NetworkManager
import io.netty.channel.ChannelHandlerContext

class PlayerNetworkManager: NetworkManager() {

}

fun handleHandshake(packet: ServerboundHandshakePacket, networkManager: NetworkManager, connection: ChannelHandlerContext) {

}