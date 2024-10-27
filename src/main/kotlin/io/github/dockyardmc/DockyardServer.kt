package io.github.dockyardmc

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.annotations.AnnotationProcessor
import io.github.dockyardmc.config.Config
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerFinishLoadEvent
import io.github.dockyardmc.events.WorldFinishLoadingEvent
import io.github.dockyardmc.implementations.commands.DockyardCommands
import io.github.dockyardmc.npc.NpcCommand
import io.github.dockyardmc.protocol.PacketParser
import io.github.dockyardmc.protocol.packets.registry.ClientPacketRegistry
import io.github.dockyardmc.protocol.packets.registry.ServerPacketRegistry
import io.github.dockyardmc.registry.MinecraftVersions
import io.github.dockyardmc.registry.RegistryManager
import io.github.dockyardmc.registry.registries.*
import io.github.dockyardmc.server.PlayerKeepAliveTimer
import io.github.dockyardmc.server.NettyServer
import io.github.dockyardmc.server.ServerTickManager
import io.github.dockyardmc.utils.Resources
import io.github.dockyardmc.world.WorldManager

class DockyardServer(configBuilder: Config.() -> Unit) {

    val config: Config = Config()
    val nettyServer: NettyServer = NettyServer(this)
    val serverTickManager: ServerTickManager = ServerTickManager()
    val playerKeepAliveTimer: PlayerKeepAliveTimer = PlayerKeepAliveTimer()

    init {
        instance = this
        configBuilder.invoke(config)

        ServerPacketRegistry.load()
        ClientPacketRegistry.load()

        RegistryManager.register(BlockRegistry)
        RegistryManager.register(EntityTypeRegistry)
        RegistryManager.register(DimensionTypeRegistry)
        RegistryManager.register(WolfVariantRegistry)
        RegistryManager.register(BannerPatternRegistry)
        RegistryManager.register(DamageTypeRegistry)
        RegistryManager.register(JukeboxSongRegistry)
        RegistryManager.register(TrimMaterialRegistry)
        RegistryManager.register(TrimPatternRegistry)
        RegistryManager.register(ChatTypeRegistry)
        RegistryManager.register(ParticleRegistry)
        RegistryManager.register(PaintingVariantRegistry)
        RegistryManager.register(PotionEffectRegistry)
        RegistryManager.register(BiomeRegistry)
        RegistryManager.register(ItemRegistry)

        if(ConfigManager.config.implementationConfig.dockyardCommands) DockyardCommands()
        if(ConfigManager.config.implementationConfig.npcCommand) NpcCommand()

        WorldManager.loadDefaultWorld()

        log("DockyardMC finished loading", LogType.SUCCESS)
        Events.dispatch(ServerFinishLoadEvent(this))
    }

    val ip get() = config.ip
    val port get() = config.port

    fun start() {
        versionInfo = Resources.getDockyardVersion()
        log("Starting DockyardMC Version ${versionInfo.dockyardVersion} (${versionInfo.gitCommit}@${versionInfo.gitBranch} for MC ${minecraftVersion.versionName})", LogType.RUNTIME)
        log("DockyardMC is still under heavy development. Things will break (I warned you)", LogType.WARNING)

        serverTickManager.start()
        playerKeepAliveTimer.start()
        nettyServer.start()

        Events.dispatch(WorldFinishLoadingEvent(WorldManager.mainWorld))
    }

    companion object {
        lateinit var versionInfo: Resources.DockyardVersionInfo
        lateinit var instance: DockyardServer
        val minecraftVersion = MinecraftVersions.v1_21_3
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
            "ServerboundClientTickEndPacket"
        )
    }
}