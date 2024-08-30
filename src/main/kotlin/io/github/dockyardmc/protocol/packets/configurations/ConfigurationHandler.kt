package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.FeatureFlags
import io.github.dockyardmc.commands.buildCommandGraph
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.runnables.runLaterAsync
import io.github.dockyardmc.team.TeamManager
import io.github.dockyardmc.serverlinks.ServerLinks
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import io.netty.channel.ChannelHandlerContext

class ConfigurationHandler(val processor: PacketProcessor): PacketHandler(processor) {

    fun handlePluginMessage(packet: ServerboundPluginMessagePacket, connection: ChannelHandlerContext) {
        val event = PluginMessageReceivedEvent(packet.channel, packet.data)
        Events.dispatch(event)
        processor.player.brand = event.data

        // Send server brand
        val serverBrandEvent = ServerBrandEvent("§bDockyardMC Server §7(https://github.com/DockyardMC/)")
        Events.dispatch(serverBrandEvent)
        connection.sendPacket(ClientboundPluginMessagePacket("minecraft:brand", serverBrandEvent.brand))

        // Send feature flags
        val featureFlagsEvent = ServerFeatureFlagsEvent(FeatureFlags.enabledFeatureFlags)
        Events.dispatch(featureFlagsEvent)
        connection.sendPacket(ClientboundFeatureFlagsPacket(featureFlagsEvent.featureFlags))

        // Send registries
        val registryPackets: MutableList<Registry> = mutableListOf(
            BannerPatterns.registryCache,
            ChatTypes.registryCache,
            DamageTypes.registryCache,
            DimensionTypes.registryCache,
            PaintingVariants.registryCache,
            WolfVariants.registryCache,
            Biomes.registryCache
        )

        registryPackets.forEach { connection.sendPacket(ClientboundRegistryDataPacket(it)) }

        connection.sendPacket(ClientboundConfigurationServerLinksPacket(ServerLinks.links))

        val finishConfigurationPacket = ClientboundFinishConfigurationPacket()
        connection.sendPacket(finishConfigurationPacket)
    }

    fun handleClientInformation(packet: ServerboundClientInformationPacket, connection: ChannelHandlerContext) {
        val clientConfiguration = ClientConfiguration(
            packet.locale,
            packet.viewDistance,
            packet.chatMode,
            packet.chatColors,
            packet.displayedSkinParts,
            packet.mainHandSide,
            packet.enableTextFiltering,
            packet.allowServerListing,
        )

        val event = PlayerClientConfigurationEvent(clientConfiguration, processor.player)
        Events.dispatch(event)
        processor.player.clientConfiguration = event.configuration
    }

    fun handleConfigurationFinishAcknowledge(packet: ServerboundFinishConfigurationAcknowledgePacket, connection: ChannelHandlerContext) {
        val player = processor.player
        processor.state = ProtocolState.PLAY
        processor.player.releaseMessagesQueue()

        val event = PlayerPreSpawnWorldSelectionEvent(player, WorldManager.getOrThrow("main"))
        Events.dispatch(event)
        val world = event.world

        processor.player.world = world
        player.gameMode.value = GameMode.ADVENTURE

        if(world.canBeJoined.value) {
            acceptPlayer(player, world)
        } else {
            world.canBeJoined.valueChanged {
                if(it.newValue) acceptPlayer(player, world)
            }
        }
    }

    private fun acceptPlayer(player: Player, world: World) {

        val chunkCenterChunkPacket = ClientboundSetCenterChunkPacket(0, 0)
        player.sendPacket(chunkCenterChunkPacket)

        val gameEventPacket = ClientboundPlayerGameEventPacket(GameEvent.START_WAITING_FOR_CHUNKS, 1f)
        player.sendPacket(gameEventPacket)

        val playPacket = ClientboundLoginPlayPacket(
            entityId = player.entityId,
            isHardcore = world.isHardcore,
            dimensionNames = WorldManager.worlds.keys,
            maxPlayers = 20,
            viewDistance = 16,
            simulationDistance = 16,
            reducedDebugInfo = false,
            enableRespawnScreen = true,
            doLimitedCrafting = false,
            dimensionType = world.dimensionType.id,
            dimensionName = world.name,
            hashedSeed = world.seed,
            gameMode = player.gameMode.value,
            previousGameMode = player.gameMode.value,
            isDebug = false,
            isFlat = true,
            portalCooldown = 0
        )
        player.sendPacket(playPacket)

        world.join(player)

        Events.dispatch(PlayerJoinEvent(processor.player))

        player.sendPacket(ClientboundCommandsPacket(buildCommandGraph(player)))

        val tickingStatePacket = ClientboundSetTickingStatePacket(DockyardServer.tickRate, false)
        player.sendPacket(tickingStatePacket)

        TeamManager.teams.values.forEach { team ->
            player.sendPacket(ClientboundTeamsPacket(CreateTeamPacketAction(team.value)))
        }

        player.setSkin(player.uuid)
    }
}