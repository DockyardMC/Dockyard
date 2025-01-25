package io.github.dockyardmc.socket

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.socket.encoders.PacketLengthEncoder
import io.github.dockyardmc.socket.decoders.PacketLengthDecoder
import io.github.dockyardmc.protocol.encoders.RawPacketEncoder
import io.github.dockyardmc.socket.decoders.RawPacketDecoder
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.lang.Void
import java.net.InetSocketAddress
import java.util.concurrent.CompletableFuture

class NettyServer(val instance: NetworkServerInstance, val networkManager: NetworkManager) {

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()

    fun start(): CompletableFuture<Void> {
        return CompletableFuture.supplyAsync {
            val bootstrap = ServerBootstrap()
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        val pipeline = ch.pipeline()
                            .addFirst(ChannelHandlers.RAW_PACKET_ENCODER, RawPacketEncoder(networkManager))
                            .addFirst(ChannelHandlers.RAW_PACKET_DECODER, RawPacketDecoder(networkManager))

                            .addBefore(ChannelHandlers.RAW_PACKET_DECODER, ChannelHandlers.PACKET_LENGTH_DECODER, PacketLengthDecoder())
                            .addBefore(ChannelHandlers.RAW_PACKET_ENCODER, ChannelHandlers.PACKET_LENGTH_ENCODER, PacketLengthEncoder())

                            .addLast(ChannelHandlers.PLAYER_NETWORK_MANAGER, networkManager)
                    }
                })

            log("${instance::class.simpleName} server running on ${instance.ip}:${instance.port}", LogType.SUCCESS)

            bootstrap.bind(InetSocketAddress(instance.ip, instance.port)).await()
            null
        }
    }
}