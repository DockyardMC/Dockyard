package io.github.dockyardmc.server

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerStartEvent
import io.github.dockyardmc.protocol.ChannelHandlers
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.decoders.PacketLengthDecoder
import io.github.dockyardmc.protocol.decoders.RawPacketDecoder
import io.github.dockyardmc.protocol.encoders.PacketLengthEncoder
import io.github.dockyardmc.protocol.encoders.RawPacketEncoder
import io.github.dockyardmc.utils.isAddressInUse
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.InetSocketAddress
import java.util.concurrent.CompletableFuture
import kotlin.system.exitProcess

class NettyServer(val instance: DockyardServer) {

    val bossGroup = NioEventLoopGroup()
    val workerGroup = NioEventLoopGroup()

    fun start(): CompletableFuture<Void> {
        return CompletableFuture.supplyAsync {
            val bootstrap = ServerBootstrap()
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        val playerNetworkManager = PlayerNetworkManager()
                        val pipeline = ch.pipeline()
                            //encoders
                            .addFirst(ChannelHandlers.RAW_PACKET_ENCODER, RawPacketEncoder())
                            .addFirst(ChannelHandlers.RAW_PACKET_DECODER, RawPacketDecoder(playerNetworkManager))

                            .addBefore(ChannelHandlers.RAW_PACKET_DECODER, ChannelHandlers.PACKET_LENGTH_DECODER, PacketLengthDecoder())
                            .addBefore(ChannelHandlers.RAW_PACKET_ENCODER, ChannelHandlers.PACKET_LENGTH_ENCODER, PacketLengthEncoder())

                            .addLast(ChannelHandlers.PLAYER_NETWORK_MANAGER, playerNetworkManager)
                    }
                })


            if (isAddressInUse(instance.ip, instance.port)) {
                log("Address ${instance.ip}:${instance.port} is already in use!", LogType.ERROR)
                exitProcess(0)
            }

            bootstrap.bind(InetSocketAddress(instance.ip, instance.port)).await()

            log("DockyardMC server running on ${instance.ip}:${instance.port}", LogType.SUCCESS)
            Events.dispatch(ServerStartEvent())

            null
        }
    }
}