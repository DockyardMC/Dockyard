package io.github.dockyardmc

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerFinishLoadEvent
import io.github.dockyardmc.events.ServerStartEvent
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.encryption.PacketEncryptionHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.ChannelPipeline
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import log

class DockyardServer(var port: Int) {

    lateinit var bootstrap: ServerBootstrap
    lateinit var channelPipeline: ChannelPipeline
    val bossGroup = NioEventLoopGroup()
    val workerGroup = NioEventLoopGroup()

    fun start() {
        log("Starting DockyardMC Version $version", LogType.DEBUG)
        if(version < 1) log("This is development build of DockyardMC. Things will break", LogType.WARNING)

        runPacketServer()
    }

    fun stop() {

    }

    private fun load() {
        log("DockyardMC finished loading", LogType.SUCCESS)
        Events.dispatch(ServerFinishLoadEvent(this))
    }

    @Throws(Exception::class)
    private fun runPacketServer() {
        try {
            bootstrap = ServerBootstrap()
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>(){
                    override fun initChannel(ch: SocketChannel) {
                        channelPipeline = ch.pipeline()
                            .addLast("processor", PacketProcessor())
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
            log("Packet server running on port $port", LogType.SUCCESS)
            Events.dispatch(ServerStartEvent(this))
            load()

            val future = bootstrap.bind(port).sync()
            future.channel().closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }
}