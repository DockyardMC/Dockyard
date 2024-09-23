package io.github.dockyardmc

import cz.lukynka.prettylog.LogType
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerFinishLoadEvent
import io.github.dockyardmc.events.ServerStartEvent
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.implementations.commands.DockyardCommands
import io.github.dockyardmc.profiler.Profiler
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundKeepAlivePacket
import io.github.dockyardmc.runnables.RepeatingTimerAsync
import io.github.dockyardmc.utils.Resources
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.ChannelPipeline
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import cz.lukynka.prettylog.log
import io.github.dockyardmc.annotations.AnnotationProcessor
import io.github.dockyardmc.config.Config
import io.github.dockyardmc.protocol.PacketParser
import java.net.InetSocketAddress
import java.util.*

class DockyardServer(configBuilder: Config.() -> Unit) {

    lateinit var bootstrap: ServerBootstrap
    lateinit var channelPipeline: ChannelPipeline
    val bossGroup = NioEventLoopGroup()
    val workerGroup = NioEventLoopGroup()

    val config = Config()

    val ip get() = config.ip
    val port get() = config.port

    // Server ticks
    val tickProfiler = Profiler()
    var serverTicks: Int = 0
    val tickTimer = RepeatingTimerAsync(50) {
        tickProfiler.start("Tick", 5)
        serverTicks++
        Events.dispatch(ServerTickEvent(serverTicks))
        tickProfiler.end()
    }

    init {
        instance = this
        configBuilder.invoke(config)
    }

    //TODO rewrite and make good
    var keepAliveId = 0L
    val keepAlivePacketTimer = RepeatingTimerAsync(5000) {
        PlayerManager.players.forEach {
            it.connection.sendPacket(ClientboundKeepAlivePacket(keepAliveId))
            val processor = PlayerManager.playerToProcessorMap[it.uuid]!!
            if(!processor.respondedToLastKeepAlive) {
                log("$it failed to respond to keep alive", LogType.WARNING)
//                it.kick(getSystemKickMessage(KickReason.FAILED_KEEP_ALIVE))
                return@forEach
            }
            processor.respondedToLastKeepAlive = false
        }
        keepAliveId++
    }

    fun start() {
        versionInfo = Resources.getDockyardVersion()
        log("Starting DockyardMC Version ${versionInfo.dockyardVersion} (${versionInfo.gitCommit}@${versionInfo.gitBranch} for MC ${versionInfo.minecraftVersion})", LogType.RUNTIME)
        log("DockyardMC is still under heavy development. Things will break (I warned you)", LogType.WARNING)

        runPacketServer()
    }

    private fun load() {
        val profiler = Profiler()
        val innerProfiler = Profiler()
        profiler.start("DockyardMC Load")
        tickTimer.run()

        val packetClasses = AnnotationProcessor.getServerboundPacketClassInfo()
        PacketParser.idAndStatePairToPacketClass = packetClasses

        AnnotationProcessor.addIdsToClientboundPackets()

        innerProfiler.start("Load Default Implementations")
        val implementationsConfig = config.implementationConfig
        if(implementationsConfig.dockyardCommands) DockyardCommands()

        innerProfiler.end()

        log("DockyardMC finished loading", LogType.SUCCESS)
        Events.dispatch(ServerFinishLoadEvent(this))
        keepAlivePacketTimer.run()

        profiler.end()
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
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            log("DockyardMC server running on $ip:$port", LogType.SUCCESS)
            Events.dispatch(ServerStartEvent(this))
            load()

            val future = bootstrap.bind(InetSocketAddress(ip, port)).sync()
            future.channel().closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }

    companion object {
        lateinit var versionInfo: Resources.DockyardVersionInfo
        lateinit var instance: DockyardServer
        var allowAnyVersion: Boolean = false

        var tickRate: Int = 20
        val debug get() = ConfigManager.config.debug

        var mutePacketLogs = mutableListOf(
            "ClientboundSystemChatMessagePacket",
            "ServerboundSetPlayerPositionPacket",
            "ServerboundSetPlayerPositionAndRotationPacket",
            "ServerboundSetPlayerRotationPacket",
            "ClientboundKeepAlivePacket",
            "ServerboundKeepAlivePacket",
            "ClientboundUpdateEntityPositionPacket",
            "ClientboundUpdateEntityPositionAndRotationPacket",
            "ClientboundUpdateEntityRotationPacket",
            "ClientboundSetHeadYawPacket",
            "ClientboundSendParticlePacket",
            "ClientboundUpdateScorePacket",
            "ClientboundChunkDataPacket",
        )
    }
}