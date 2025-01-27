package io.github.dockyardmc.socket

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.socket.encoders.PacketLengthEncoder
import io.github.dockyardmc.socket.decoders.PacketLengthDecoder
import io.github.dockyardmc.socket.encoders.RawPacketEncoder
import io.github.dockyardmc.socket.decoders.RawPacketDecoder
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.lang.IllegalStateException
import java.lang.Void
import java.net.InetSocketAddress
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class NettyServer(val ip: String, val port: Int, val networkManager: KClass<out NetworkManager>) {

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()

    fun start(): CompletableFuture<Void> {
        return CompletableFuture.supplyAsync {
            val bootstrap = ServerBootstrap()
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        val networkManagerInstance = networkManager.primaryConstructor?.call() ?: throw IllegalStateException("Could not initialize network manager instance")
                        val pipeline = ch.pipeline()
                            .addFirst(ChannelHandlers.RAW_PACKET_ENCODER, RawPacketEncoder(networkManagerInstance))
                            .addFirst(ChannelHandlers.RAW_PACKET_DECODER, RawPacketDecoder(networkManagerInstance))

                            .addBefore(ChannelHandlers.RAW_PACKET_DECODER, ChannelHandlers.PACKET_LENGTH_DECODER, PacketLengthDecoder())
                            .addBefore(ChannelHandlers.RAW_PACKET_ENCODER, ChannelHandlers.PACKET_LENGTH_ENCODER, PacketLengthEncoder())

                            .addLast(ChannelHandlers.PLAYER_NETWORK_MANAGER, networkManagerInstance)
                    }
                })

            log("Server running on ${ip}:${port}", LogType.SUCCESS)

            bootstrap.bind(InetSocketAddress(ip, port)).await()
            null
        }
    }
}