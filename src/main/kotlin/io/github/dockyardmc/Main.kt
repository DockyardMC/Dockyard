package io.github.dockyardmc

import LogType
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import log

private const val port = 25565
const val version = 0.1

fun main(args: Array<String>) {
    log("Starting DockyardMC Version $version", LogType.DEBUG)
    if(version < 1) log("This is development build of DockyardMC. Things will break", LogType.WARNING)
    run()
}

@Throws(Exception::class)
fun run() {
    val bossGroup = NioEventLoopGroup()
    val workerGroup = NioEventLoopGroup()
    try {
        val bootstrap = ServerBootstrap()
        bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<SocketChannel>(){
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline()
                        .addLast(PacketProcessor())
                }
            }).option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
        log("Packet server running on port $port", LogType.SUCCESS)

        val future = bootstrap.bind(port).sync()
        future.channel().closeFuture().sync()
    } finally {
        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }
}