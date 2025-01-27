package io.github.dockyardmc.sailboat

import cz.lukynka.prettylog.log
import io.github.dockyardmc.protocol.ProtocolState
import io.github.dockyardmc.protocol.packets.handshake.serverbound.ServerboundHandshakePacket
import io.github.dockyardmc.protocol.packets.status.clientbound.ClientboundStatusResponsePacket
import io.github.dockyardmc.protocol.packets.status.serverbound.ServerboundStatusRequestPacket
import io.github.dockyardmc.protocol.types.Players
import io.github.dockyardmc.protocol.types.ServerStatus
import io.github.dockyardmc.protocol.types.Version
import io.github.dockyardmc.protocol.types.toJson
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.socket.NetworkManager
import io.github.dockyardmc.socket.PacketRegistry
import io.netty.channel.ChannelHandlerContext

object ServerPacketRegistry : PacketRegistry() {

    override fun load() {
        addHandshake(ServerboundHandshakePacket::class, ::handleHandshake)

        addStatus(ServerboundStatusRequestPacket::class, ::handleStatusRequest)
    }
}


fun handleHandshake(packet: ServerboundHandshakePacket, networkManager: NetworkManager, connection: ChannelHandlerContext) {
    log("Received ${packet::class.simpleName}")
    networkManager.protocolState = ProtocolState.STATUS
}

fun handleStatusRequest(packet: ServerboundStatusRequestPacket, networkManager: NetworkManager, connection: ChannelHandlerContext) {
    log("Received ${packet::class.simpleName}")

    val status = ServerStatus(
        version = Version("Phaethon", 3),
        players = Players(1, 0, mutableListOf()),
        description = "<orange>The best sibling proxies!".toComponent(),
        enforceSecureChat = false,
        previewsChat = false,
        favicon = "data:image/png;base64",
    ).toJson()

    println(status)
    networkManager.sendPacket(ClientboundStatusResponsePacket(status), connection)
}
