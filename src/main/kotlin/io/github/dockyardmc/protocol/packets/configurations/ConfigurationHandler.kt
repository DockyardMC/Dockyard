package io.github.dockyardmc.protocol.packets.configurations

import LogType
import io.github.dockyardmc.FeatureFlags
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.login.ClientboundChangeDifficultyPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.utils.Resources
import io.github.dockyardmc.world.Difficulty
import io.github.dockyardmc.world.WorldManager
import io.netty.channel.ChannelHandlerContext
import log

class ConfigurationHandler(val processor: PacketProcessor): PacketHandler(processor) {

    fun handlePluginMessage(packet: ServerboundPluginMessagePacket, connection: ChannelHandlerContext) {
        log("Received ${processor.player}'s client brand: ${packet.data}", LogType.DEBUG)

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

        val registryDataPacket = ClientboundRegistryDataPacket(Resources.registry)
        connection.sendPacket(registryDataPacket)

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
        log("Configuration Finish Acknowledged", LogType.SUCCESS)
        processor.state = ProtocolState.PLAY
        processor.player.releaseMessagesQueue()

        val entityId = PlayerManager.entityCounter.incrementAndGet()
        processor.player.entityId = entityId
        PlayerManager.playerToEntityIdMap[entityId] = processor.player

        val world = WorldManager.worlds[0]
        processor.player.world = world

        val playPacket = ClientboundPlayPacket(
            entityId,
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
            GameMode.CREATIVE,
            GameMode.SURVIVAL,
            false,
            true,
            0
        )

        connection.sendPacket(playPacket)

//        val difficultyPacket = ClientboundChangeDifficultyPacket(Difficulty.PEACEFUL, false)
//        connection.sendPacket(difficultyPacket)

        val chunkCenterChunkPacket = ClientboundSetCenterChunkPacket(0, 0)
        connection.sendPacket(chunkCenterChunkPacket)

        val gameEventPacket = ClientboundPlayerGameEventPacket(GameEvent.START_WAITING_FOR_CHUNKS, 1f)
        connection.sendPacket(gameEventPacket)

        log("chunk packet")
        processor.player.world!!.chunks.forEach {
            connection.sendPacket(it.packet)
        }

        connection.sendPacket(ClientboundPlayerSynchronizePositionPacket(Location(0, 0, 0), 8743))
        connection.sendPacket(ClientboundRespawnPacket())

    }
}