package io.github.dockyardmc.sailboat.server

import io.github.dockyardmc.sailboat.ClientPacketRegistry
import io.github.dockyardmc.sailboat.PlayerNetworkManager
import io.github.dockyardmc.sailboat.ServerPacketRegistry
import io.github.dockyardmc.socket.NettyServer
import io.github.dockyardmc.socket.NetworkServerInstance

class PhaethonServer(ip: String, port: Int): NetworkServerInstance(ip, port) {
    val nettyServer = NettyServer(ip, port, PlayerNetworkManager::class)

    fun start() {
        ServerPacketRegistry.load()
        ClientPacketRegistry.load()
        nettyServer.start().join()
    }

    companion object {

    }
}