package io.github.dockyardmc

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerFinishLoadEvent
import io.github.dockyardmc.events.ServerStartEvent
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.motd.Players
import io.github.dockyardmc.motd.ServerStatus
import io.github.dockyardmc.motd.Version
import io.github.dockyardmc.motd.toJson
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.player.kick.KickReason
import io.github.dockyardmc.player.kick.getSystemKickMessage
import io.github.dockyardmc.plugins.PluginManager
import io.github.dockyardmc.plugins.bundled.DockyardCommands
import io.github.dockyardmc.plugins.bundled.MayaTestPlugin
import io.github.dockyardmc.profiler.Profiler
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundKeepAlivePacket
import io.github.dockyardmc.runnables.RepeatingTimerAsync
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.Console
import io.github.dockyardmc.utils.Resources
import io.github.dockyardmc.utils.VersionToProtocolVersion
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.ChannelPipeline
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import log
import java.io.File
import java.util.*

class DockyardServer(var port: Int) {

    lateinit var bootstrap: ServerBootstrap
    lateinit var channelPipeline: ChannelPipeline
    val bossGroup = NioEventLoopGroup(3)
    val workerGroup = NioEventLoopGroup()

    // Server ticks
    val tickProfiler = Profiler()
    val tickTimer = RepeatingTimerAsync(50) {
        tickProfiler.start("Tick", 5)
        Events.dispatch(ServerTickEvent())
        tickProfiler.end()
    }

    //TODO rewrite and make good
//    var keepAliveId = 0L
//    val keepAlivePacketTimer = RepeatingTimerAsync(5000) {
//        PlayerManager.players.forEach {
//            it.connection.sendPacket(ClientboundKeepAlivePacket(keepAliveId))
//            val processor = PlayerManager.playerToProcessorMap[it.uuid]!!
//            if(!processor.respondedToLastKeepAlive) {
//                log("$it failed to respond to keep alive", LogType.WARNING)
//                it.kick(getSystemKickMessage(KickReason.FAILED_KEEP_ALIVE))
//                return@forEach
//            }
//            processor.respondedToLastKeepAlive = false
//        }
//        keepAliveId++
//    }

    fun start() {
        versionInfo = Resources.getDockyardVersion()

        log("Starting DockyardMC Version ${versionInfo.dockyardVersion} (${versionInfo.gitCommit}@${versionInfo.gitBranch} for MC ${versionInfo.minecraftVersion})", LogType.RUNTIME)
        if(versionInfo.dockyardVersion.toDouble() < 1) log("This is development build of DockyardMC. Things will break", LogType.WARNING)

        runPacketServer()
    }

    fun stop() {

    }

    private fun load() {
        val profiler = Profiler()
        val innerProfiler = Profiler()
        profiler.start("DockyardMC Load")
        tickTimer.run()

        innerProfiler.start("Load World")
        val mainWorld = World("world")
        mainWorld.worldBorder.diameter = 1000.0
        mainWorld.defaultSpawnLocation = Location(0, 201, 0)
        WorldManager.worlds.add(mainWorld)
        innerProfiler.end()

        innerProfiler.start("Load Plugins")
        PluginManager.loadLocal(DockyardCommands())
        PluginManager.loadLocal(MayaTestPlugin())
        innerProfiler.end()

        log("DockyardMC finished loading", LogType.SUCCESS)
        Events.dispatch(ServerFinishLoadEvent(this))

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
            log("DockyardMC server running on port $port", LogType.SUCCESS)
            Events.dispatch(ServerStartEvent(this))
            load()

            val future = bootstrap.bind(port).sync()
            future.channel().closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }

    companion object {
        fun broadcastMessage(message: String) { this.broadcastMessage(message.toComponent()) }
        fun broadcastMessage(component: Component) { PlayerManager.players.sendMessage(component); Console.sendMessage(component.stripStyling()) }
        fun broadcastActionBar(message: String) { this.broadcastActionBar(message.toComponent()) }
        fun broadcastActionBar(component: Component) { PlayerManager.players.sendActionBar(component) }
        lateinit var versionInfo: Resources.DockyardVersionInfo
        var allowAnyVersion: Boolean = false

        var tickRate: Float = 20f

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
            "ClientboundSetHeadYawPacket"
        )
    }
}