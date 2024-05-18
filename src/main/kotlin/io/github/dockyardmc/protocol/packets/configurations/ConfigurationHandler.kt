package io.github.dockyardmc.protocol.packets.configurations

import LogType
import io.github.dockyardmc.FeatureFlags
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.ClientConfiguration
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.login.ClientboundChangeDifficultyPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerSynchronizePositionPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundRespawnPacket
import io.github.dockyardmc.utils.Resources
import io.github.dockyardmc.world.Difficulty
import io.github.dockyardmc.world.WorldManager
import io.netty.channel.ChannelHandlerContext
import log
import org.jglrxavpok.hephaistos.parser.SNBTParser
import java.io.FileReader


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

        val parser = SNBTParser(FileReader(Resources.getFile("registry.snbt")))
        val nbt = parser.parse()

        val registryDataPacket = ClientboundRegistryDataPacket(nbt)
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

        val playPacket = ClientboundPlayPacket(
            PlayerManager.entityCounter.incrementAndGet(),
            false,
            mutableListOf("world"),
            20,
            16,
            16,
            false,
            true,
            false,
            "overworld",
            WorldManager.worlds[0].name,
            WorldManager.worlds[0].seed,
            GameMode.CREATIVE,
            GameMode.SURVIVAL,
            false,
            true,
            0
        )

        connection.sendPacket(playPacket)

        val difficultyPacket = ClientboundChangeDifficultyPacket(Difficulty.PEACEFUL, false)
        connection.sendPacket(difficultyPacket)

        connection.sendPacket(ClientboundRespawnPacket())
        connection.sendPacket(ClientboundPlayerSynchronizePositionPacket(Location(5.0, 10.0, 309.0), 8743))
    }
}