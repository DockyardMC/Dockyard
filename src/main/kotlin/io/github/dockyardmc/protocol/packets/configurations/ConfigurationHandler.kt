package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.server.FeatureFlags
import io.github.dockyardmc.commands.buildCommandGraph
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.motd.ServerStatusManager
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.team.TeamManager
import io.github.dockyardmc.serverlinks.ServerLinks
import io.github.dockyardmc.protocol.plugin.PluginMessages
import io.github.dockyardmc.protocol.plugin.messages.BrandPluginMessage
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import io.netty.channel.ChannelHandlerContext

class ConfigurationHandler(val processor: PlayerNetworkManager): PacketHandler(processor) {

    fun handlePluginMessage(packet: ServerboundConfigurationPluginMessagePacket, connection: ChannelHandlerContext) {
        val event = PluginMessageReceivedEvent(processor.player, packet.channel, packet.data)
        Events.dispatch(event)

        if(!event.cancelled) PluginMessages.handle(event.channel, event.data, processor.player)

        // Send server brand
        val serverBrandEvent = ServerBrandEvent("§bDockyardMC Server §7(https://github.com/DockyardMC/)§r")
        Events.dispatch(serverBrandEvent)
        connection.sendPacket(BrandPluginMessage(serverBrandEvent.brand).asConfigPacket("minecraft:brand"), processor)

        // Send feature flags
        val featureFlagsEvent = PlayerSendFeatureFlagsEvent(FeatureFlags.enabledFeatureFlags)
        Events.dispatch(featureFlagsEvent)
        connection.sendPacket(ClientboundFeatureFlagsPacket(featureFlagsEvent.featureFlags), processor)

        connection.sendPacket(ClientboundUpdateTagsPacket(), processor)

        RegistryManager.dynamicRegistries.forEach { connection.sendPacket(ClientboundRegistryDataPacket(it), processor) }
        connection.sendPacket(ClientboundConfigurationServerLinksPacket(ServerLinks.links), processor)

        val finishConfigurationPacket = ClientboundFinishConfigurationPacket()
        connection.sendPacket(finishConfigurationPacket, processor)
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
            packet.particleSettings
        )

        val event = PlayerClientConfigurationEvent(clientConfiguration, processor.player)
        Events.dispatch(event)
        processor.player.clientConfiguration = event.configuration
    }

    fun handleConfigurationFinishAcknowledge(packet: ServerboundFinishConfigurationAcknowledgePacket, connection: ChannelHandlerContext) {
        val player = processor.player
        processor.state = ProtocolState.PLAY
        processor.player.releaseMessagesQueue()

        val event = PlayerSpawnEvent(player, WorldManager.getOrThrow("main"))
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

        val gameEventPacket = ClientboundGameEventPacket(GameEvent.START_WAITING_FOR_CHUNKS, 1f)
        player.sendPacket(gameEventPacket)

        val playPacket = ClientboundPlayPacket(
            entityId = player.entityId,
            isHardcore = world.isHardcore,
            dimensionNames = WorldManager.worlds.keys,
            maxPlayers = ConfigManager.config.maxPlayers,
            viewDistance = 16,
            simulationDistance = 16,
            reducedDebugInfo = false,
            enableRespawnScreen = true,
            doLimitedCrafting = false,
            dimensionType = world.dimensionType.getProtocolId(),
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
        ServerStatusManager.updateCache()
        Events.dispatch(PlayerJoinEvent(processor.player))

        player.sendPacket(ClientboundCommandsPacket(buildCommandGraph(player)))

        val tickingStatePacket = ClientboundSetTickingStatePacket(DockyardServer.tickRate, false)
        player.sendPacket(tickingStatePacket)

        TeamManager.teams.values.forEach { team ->
            player.sendPacket(ClientboundTeamsPacket(CreateTeamPacketAction(team.value)))
        }

        player.sendPacket(ClientboundPlayerInfoUpdatePacket(PlayerInfoUpdate(player.uuid, SetListedInfoUpdateAction(player.isListed.value))))
        if(ConfigManager.config.useMojangAuth) player.setSkin(player.uuid)
    }
}