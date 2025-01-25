package io.github.dockyardmc.socket

import io.github.dockyardmc.protocol.ProtocolState
import io.github.dockyardmc.protocol.WrappedPacket
import io.github.dockyardmc.scroll.Component
import io.ktor.util.network.*
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

abstract class NetworkManager(val serverPacketRegistry: PacketRegistry, val clientPacketRegistry: PacketRegistry): ChannelInboundHandlerAdapter() {
    var encryptionEnabled: Boolean = false
    var compressionEnabled: Boolean = false

    lateinit var player: NetworkPlayer
    lateinit var address: String
    lateinit var connection: ChannelHandlerContext

    var playerProtocolVersion: Int = 0
    var protocolState: ProtocolState = ProtocolState.HANDSHAKE

    abstract fun getServerCompressionThreshold(): Int

    override fun channelRead(connection: ChannelHandlerContext, msg: Any) {
        if (!this::address.isInitialized) address = connection.channel().remoteAddress().address

        val packet = msg as WrappedPacket
        handlePacket(connection, packet)
    }

    abstract fun handlePacket(connection: ChannelHandlerContext, packet: WrappedPacket)

    abstract fun kick(message: Component, connection: ChannelHandlerContext)

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    fun isPlayerInitialized(): Boolean {
        return ::player.isInitialized
    }

}
