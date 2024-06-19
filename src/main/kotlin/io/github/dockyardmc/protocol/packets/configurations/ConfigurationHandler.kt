package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.FeatureFlags
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundChangeDifficultyPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.world.Difficulty
import io.github.dockyardmc.world.WorldManager
import io.netty.channel.ChannelHandlerContext

class ConfigurationHandler(val processor: PacketProcessor): PacketHandler(processor) {

    fun handlePluginMessage(packet: ServerboundPluginMessagePacket, connection: ChannelHandlerContext) {
        val event = PluginMessageReceivedEvent(packet.channel, packet.data)
        Events.dispatch(event)
        processor.player.brand = event.data

        // Send server brand
        val serverBrandEvent = ServerBrandEvent("§bDockyardMC §8| §7Custom Kotlin Server Implementation")
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

        registryPackets.forEach {
            connection.sendPacket(ClientboundRegistryDataPacket(it))
        }

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

        val world = WorldManager.worlds[0]
        processor.player.world = world

        player.gameMode.value = GameMode.CREATIVE

        val playPacket = ClientboundLoginPlayPacket(
            player.entityId,
            false,
            WorldManager.worlds.map { it.name }.toMutableList(),
            20,
            16,
            16,
            false,
            true,
            false,
            "overworld",
            world.name,
            world.seed,
            player.gameMode.value,
            GameMode.SURVIVAL,
            false,
            true,
            0
        )

        player.sendPacket(playPacket)

        val difficultyPacket = ClientboundChangeDifficultyPacket(Difficulty.PEACEFUL, false)
        player.sendPacket(difficultyPacket)

        val chunkCenterChunkPacket = ClientboundSetCenterChunkPacket(0, 0)
        player.sendPacket(chunkCenterChunkPacket)

        val gameEventPacket = ClientboundPlayerGameEventPacket(GameEvent.START_WAITING_FOR_CHUNKS, 1f)
        player.sendPacket(gameEventPacket)

        processor.player.world.chunks.forEach {
            player.sendPacket(it.packet)
        }

        processor.player.location = world.defaultSpawnLocation

        player.sendPacket(ClientboundRespawnPacket())
        player.sendPacket(ClientboundPlayerSynchronizePositionPacket(world.defaultSpawnLocation))
        processor.player.isFullyInitialized = true

        //TODO Send command completion packets
//        connection.sendPacket(ClientboundCommandsPacket(mutableListOf(testCommand)))


        Events.dispatch(PlayerJoinEvent(processor.player))

        // Make player visibile to all other players by default
        PlayerManager.players.forEach { loopPlayer ->
            if(loopPlayer.username == player.username) return@forEach
            player.addViewer(loopPlayer)
            loopPlayer.addViewer(player)
        }

        //TODO add multi-world support and send this stuff when joining world
//        val worldBorder = player.world!!.worldBorder
//        val worldBorderPacket = ClientboundInitializeWorldBorderPacket(worldBorder.diameter, worldBorder.diameter, 0, worldBorder.warningBlocks, worldBorder.warningTime)
//        connection.sendPacket(worldBorderPacket)

//        connection.sendPacket(ClientboundPlayerAbilitiesPacket(isFlying = true, allowFlying = true))


        val tickingStatePacket = ClientboundSetTickingStatePacket(DockyardServer.tickRate, false)
        player.sendPacket(tickingStatePacket)

        SkinManager.updateSkinOf(player)
    }
}