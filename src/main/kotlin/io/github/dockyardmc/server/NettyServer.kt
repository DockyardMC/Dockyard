package io.github.dockyardmc.server

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerStartEvent
import io.github.dockyardmc.protocol.ChannelHandlers
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.decoders.FrameDecoder
import io.github.dockyardmc.protocol.decoders.PacketDecoder
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.lang.Void
import java.net.InetSocketAddress
import java.util.concurrent.CompletableFuture

class NettyServer(val instance: DockyardServer) {

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()

    fun start(): CompletableFuture<Void> {
        return CompletableFuture.supplyAsync {
            val bootstrap = ServerBootstrap()
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        val playerNetworkManager = PlayerNetworkManager()
                        val pipeline = ch.pipeline()
                            .addLast(ChannelHandlers.FRAME_DECODER, FrameDecoder())
                            .addLast(ChannelHandlers.PACKET_DECODER, PacketDecoder(playerNetworkManager))
                            .addLast(ChannelHandlers.PLAYER_NETWORK_MANAGER, playerNetworkManager)
                    }
                })

            log("DockyardMC server running on ${instance.ip}:${instance.port}", LogType.SUCCESS)
            Events.dispatch(ServerStartEvent())

            bootstrap.bind(InetSocketAddress(instance.ip, instance.port)).await()
            null
        }
    }
}