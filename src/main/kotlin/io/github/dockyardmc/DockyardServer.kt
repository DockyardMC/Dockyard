package io.github.dockyardmc

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.config.Config
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerFinishLoadEvent
import io.github.dockyardmc.events.WorldFinishLoadingEvent
import io.github.dockyardmc.implementations.block.DefaultBlockHandlers
import io.github.dockyardmc.implementations.commands.DefaultCommands
import io.github.dockyardmc.profiler.profiler
import io.github.dockyardmc.protocol.NetworkCompression
import io.github.dockyardmc.protocol.packets.configurations.Tag
import io.github.dockyardmc.protocol.packets.registry.ClientPacketRegistry
import io.github.dockyardmc.protocol.packets.registry.ServerPacketRegistry
import io.github.dockyardmc.registry.MinecraftVersions
import io.github.dockyardmc.registry.RegistryManager
import io.github.dockyardmc.registry.registries.*
import io.github.dockyardmc.registry.registries.tags.*
import io.github.dockyardmc.scheduler.GlobalScheduler
import io.github.dockyardmc.server.NettyServer
import io.github.dockyardmc.server.PlayerKeepAliveTimer
import io.github.dockyardmc.server.ServerTickManager
import io.github.dockyardmc.spark.SparkDockyardIntegration
import io.github.dockyardmc.utils.InstrumentationUtils
import io.github.dockyardmc.utils.Resources
import io.github.dockyardmc.utils.UpdateChecker
import io.github.dockyardmc.world.WorldManager

class DockyardServer(configBuilder: Config.() -> Unit) {

    val config: Config = Config()
    val nettyServer: NettyServer = NettyServer(this)
    val serverTickManager: ServerTickManager = ServerTickManager()
    val playerKeepAliveTimer: PlayerKeepAliveTimer = PlayerKeepAliveTimer()

    init {
        profiler("Server Load") {

            instance = this
            configBuilder.invoke(config)

            profiler("Register packets") {
                ServerPacketRegistry.load()
                ClientPacketRegistry.load()
            }

            profiler("Load Registries") {

                SoundRegistry.initialize(RegistryManager.getStreamFromPath("registry/sound_registry.json.gz"))

                RegistryManager.register<Attribute>(AttributeRegistry)
                RegistryManager.register<RegistryBlock>(BlockRegistry)
                RegistryManager.register<EntityType>(EntityTypeRegistry)
                RegistryManager.register<DimensionType>(DimensionTypeRegistry)
                RegistryManager.register<BannerPattern>(BannerPatternRegistry)
                RegistryManager.register<DamageType>(DamageTypeRegistry)
                RegistryManager.register<JukeboxSong>(JukeboxSongRegistry)
                RegistryManager.register<TrimMaterial>(TrimMaterialRegistry)
                RegistryManager.register<TrimPattern>(TrimPatternRegistry)
                RegistryManager.register<ChatType>(ChatTypeRegistry)
                RegistryManager.register<Particle>(ParticleRegistry)
                RegistryManager.register<PaintingVariant>(PaintingVariantRegistry)
                RegistryManager.register<PotionEffect>(PotionEffectRegistry)
                RegistryManager.register<Biome>(BiomeRegistry)
                RegistryManager.register<Item>(ItemRegistry)
                RegistryManager.register<Fluid>(FluidRegistry)
                RegistryManager.register<PotionType>(PotionTypeRegistry)

                RegistryManager.register<WolfVariant>(WolfVariantRegistry)
                RegistryManager.register<WolfSoundVariant>(WolfSoundVariantRegistry)
                RegistryManager.register<CatVariant>(CatVariantRegistry)
                RegistryManager.register<CowVariant>(CowVariantRegistry)
                RegistryManager.register<PigVariant>(PigVariantRegistry)
                RegistryManager.register<FrogVariant>(FrogVariantRegistry)
                RegistryManager.register<ChickenVariant>(ChickenVariantRegistry)

                RegistryManager.register<Tag>(ItemTagRegistry)
                RegistryManager.register<Tag>(BlockTagRegistry)
                RegistryManager.register<Tag>(EntityTypeTagRegistry)
                RegistryManager.register<Tag>(FluidTagRegistry)
                RegistryManager.register<Tag>(BiomeTagRegistry)

                RegistryManager.register<DialogType>(DialogTypeRegistry)
                RegistryManager.register<DialogBodyType>(DialogBodyTypeRegistry)
                RegistryManager.register<DialogEntry>(DialogRegistry)
                RegistryManager.register<DialogInputType>(DialogInputTypeRegistry)
                RegistryManager.register<DialogActionType>(DialogActionTypeRegistry)
            }

            profiler("Default Implementations") {
                if (ConfigManager.config.implementationConfig.defaultCommands) DefaultCommands().register()
                if (ConfigManager.config.implementationConfig.spark) SparkDockyardIntegration().initialize()
                if (ConfigManager.config.implementationConfig.applyBlockPlacementRules) DefaultBlockHandlers().register()
            }

            NetworkCompression.COMPRESSION_THRESHOLD = ConfigManager.config.networkCompressionThreshold
            WorldManager.loadDefaultWorld()

            Events.dispatch(ServerFinishLoadEvent(this))
            if (ConfigManager.config.updateChecker) UpdateChecker()

            if (InstrumentationUtils.isDebuggerAttached()) {
                profiler("Setup hot reload detection") {
                    InstrumentationUtils.setupHotReloadDetection()
                }
            }
        }
    }

    val ip get() = config.ip
    val port get() = config.port

    fun start() {
        versionInfo = Resources.getDockyardVersion()
        log("Starting DockyardMC Version ${versionInfo.dockyardVersion} (${versionInfo.gitCommit}@${versionInfo.gitBranch} for MC ${minecraftVersion.versionName})", LogType.RUNTIME)
        log("DockyardMC is still under heavy development. Things will break (I warned you)", LogType.WARNING)

        profiler("Start TCP socket") {
            serverTickManager.start()
            playerKeepAliveTimer.start()
            nettyServer.start()
        }

        Events.dispatch(WorldFinishLoadingEvent(WorldManager.mainWorld))
    }

    companion object {
        lateinit var versionInfo: Resources.DockyardVersionInfo
        lateinit var instance: DockyardServer
        val minecraftVersion = MinecraftVersions.v1_21_8
        var allowAnyVersion: Boolean = false

        val scheduler = GlobalScheduler("main_scheduler")

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
            "ServerboundClientTickEndPacket",
            "ClientboundEntityTeleportPacket",
            "ClientboundUnloadChunkPacket",
            "ClientboundTrackedWaypointPacket"
        )
    }
}
