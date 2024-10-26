package io.github.dockyardmc.protocol.packets

import io.github.dockyardmc.protocol.packets.configurations.*
import io.github.dockyardmc.protocol.packets.handshake.ClientboundPingResponsePacket
import io.github.dockyardmc.protocol.packets.handshake.ClientboundStatusResponsePacket
import io.github.dockyardmc.protocol.packets.login.ClientboundEncryptionRequestPacket
import io.github.dockyardmc.protocol.packets.login.ClientboundLoginDisconnectPacket
import io.github.dockyardmc.protocol.packets.login.ClientboundLoginSuccessPacket
import io.github.dockyardmc.protocol.packets.login.ClientboundSetCompressionPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object ClientPacketRegistry {

    private val handshakeCounter = AtomicInteger()
    private val statusCounter = AtomicInteger()
    private val loginCounter = AtomicInteger()
    private val configurationCounter = AtomicInteger()
    private val playCounter = AtomicInteger()

    val handshakePackets: MutableMap<KClass<*>, Int> = mutableMapOf()
    val statusPackets: MutableMap<KClass<*>, Int> = mutableMapOf()
    val loginPackets: MutableMap<KClass<*>, Int> = mutableMapOf()
    val configurationPackets: MutableMap<KClass<*>, Int> = mutableMapOf()
    val playPackets: MutableMap<KClass<*>, Int> = mutableMapOf()

    fun getIdAndStateOrThrow(packet: KClass<*>): Pair<Int, ProtocolState> {
        val handshakePacket = handshakePackets[packet]
        val statusPacket = statusPackets[packet]
        val loginPacket = loginPackets[packet]
        val configurationPacket = configurationPackets[packet]
        val playPacket = playPackets[packet]
        if(handshakePacket != null) return handshakePacket to ProtocolState.HANDSHAKE
        if(statusPacket != null) return statusPacket to ProtocolState.STATUS
        if(loginPacket != null) return loginPacket to ProtocolState.LOGIN
        if(configurationPacket != null) return configurationPacket to ProtocolState.CONFIGURATION
        if(playPacket != null) return playPacket to ProtocolState.PLAY

        throw IllegalArgumentException("Packet class ${packet.simpleName} does not have assigned protocol id!")
    }

    fun load() {
        addStatus(ClientboundStatusResponsePacket::class)
        addStatus(ClientboundPingResponsePacket::class)

        addLogin(ClientboundLoginDisconnectPacket::class)
        addLogin(ClientboundEncryptionRequestPacket::class)
        addLogin(ClientboundLoginSuccessPacket::class)
        addLogin(ClientboundSetCompressionPacket::class)
        skipLogin("Plugin Request")
        skipLogin("Cookie Request")

        skipConfiguration("Cookie Request")
        addConfiguration(ClientboundConfigurationPluginMessagePacket::class)
        skipConfiguration("Disconnect")
        addConfiguration(ClientboundFinishConfigurationPacket::class)
        skipConfiguration("Keep Alive")
        skipConfiguration("Ping")
        skipConfiguration("Reset Chat")
        addConfiguration(ClientboundRegistryDataPacket::class)
        skipConfiguration("Resourcepack Pop")
        skipConfiguration("Resourcepack Push")
        skipConfiguration("Cookie Store")
        skipConfiguration("Transfer Packet")
        addConfiguration(ClientboundFeatureFlagsPacket::class)
        addConfiguration(ClientboundUpdateTagsPacket::class)
        addConfiguration(ClientboundKnownPacksPackets::class)
        skipConfiguration("Custom Reports")
        addConfiguration(ClientboundConfigurationServerLinksPacket::class)

        skipPlay("Bundle")
        addPlay(ClientboundSpawnEntityPacket::class)
        skipPlay("Spawn experience orb")
        addPlay(ClientboundEntityAnimationPacket::class)
        skipPlay("Statistics")
        addPlay(ClientboundAcknowledgeBlockChangePacket::class)
        skipPlay("Block break animation")
        skipPlay("Block entity data")
        skipPlay("Block action")
        skipPlay("Block change")
        addPlay(ClientboundBossbarPacket::class)
        addPlay(ClientboundChangeDifficultyPacket::class)
        skipPlay("Chunk batch finished")
        skipPlay("Chunk batch start")
        skipPlay("Chunk biomes")
        addPlay(ClientboundClearTitlePacket::class)
        addPlay(ClientboundSuggestionsResponse::class)
        addPlay(ClientboundCommandsPacket::class)
        addPlay(ClientboundCloseInventoryPacket::class)
        addPlay(ClientboundSetContainerContentPacket::class)
        skipPlay("Container property")
        addPlay(ClientboundSetInventorySlotPacket::class)
        skipPlay("Cookie request")
        skipPlay("Item cooldown")
        skipPlay("Chat suggestion")
        addPlay(ClientboundPlayPluginMessagePacket::class)
        addPlay(ClientboundDamageEventPacket::class)
        skipPlay("Debug sample")
        skipPlay("Delete chat")
        addPlay(ClientboundDisconnectPacket::class)
        skipPlay("Disguised chat packet")
        addPlay(ClientboundEntityEventPacket::class)
        addPlay(ClientboundEntityPositionSyncPacket::class)
        skipPlay("Explosion")
        addPlay(ClientboundUnloadChunkPacket::class)
        addPlay(ClientboundGameEventPacket::class)
        skipPlay("open horse inventory")
        skipPlay("hit animation")
        addPlay(ClientboundInitializeWorldBorderPacket::class)
        addPlay(ClientboundKeepAlivePacket::class)
        addPlay(ClientboundChunkDataPacket::class)
        addPlay(ClientboundWorldEventPacket::class)
        addPlay(ClientboundSendParticlePacket::class)
        skipPlay("update light")
        addPlay(ClientboundLoginPacket::class)
        skipPlay("map data")
        skipPlay("trade list")
        addPlay(ClientboundUpdateEntityPositionPacket::class)
        addPlay(ClientboundUpdateEntityPositionAndRotationPacket::class)
        skipPlay("move minecart")
        addPlay(ClientboundUpdateEntityRotationPacket::class)
        skipPlay("vehicle move")
        skipPlay("open book")
        addPlay(ClientboundOpenContainerPacket::class)
        skipPlay("open signed book")
        skipPlay("ping")
        skipPlay("ping response")
        skipPlay("place ghost recipe")
        addPlay(ClientboundPlayerAbilitiesPacket::class)
        skipPlay("player chat message")
        skipPlay("end combat")
        skipPlay("enter combat")
        skipPlay("death combat")
        addPlay(ClientboundPlayerInfoRemovePacket::class)
        addPlay(ClientboundPlayerInfoUpdatePacket::class)
        skipPlay("face player")
        addPlay(ClientboundPlayerSynchronizePositionPacket::class)
        skipPlay("player rotation")
        skipPlay("recipe book add")
        skipPlay("recipe book remove")
        skipPlay("recipe book settings")
        addPlay(ClientboundEntityRemovePacket::class)
        addPlay(ClientboundRemoveEntityEffectPacket::class)
        addPlay(ClientboundResetScorePacket::class)
        addPlay(ClientboundRemoveResourcepackPacket::class)
        addPlay(ClientboundAddResourcepackPacket::class)
        addPlay(ClientboundRespawnPacket::class)
        addPlay(ClientboundSetHeadYawPacket::class)
        skipPlay("multi block change")
        skipPlay("select advancements tab")
        skipPlay("server data")
        skipPlay("actionbar")
        skipPlay("world border center")
        skipPlay("world border lerp size")
        skipPlay("world border size")
        skipPlay("world border warning delay")
        addPlay(ClientboundSetWorldBorderWarningDistance::class)
        skipPlay("camera")
        addPlay(ClientboundSetCenterChunkPacket::class)
        skipPlay("view distance")
        skipPlay("cursor item")
        skipPlay("spawn position")
        addPlay(ClientboundDisplayObjectivePacket::class)
        addPlay(ClientboundSetEntityMetadataPacket::class)
        skipPlay("attach entity packet")
        addPlay(ClientboundSetEntityVelocityPacket::class)
        addPlay(ClientboundSetEntityEquipmentPacket::class)
        addPlay(ClientboundSetExperiencePacket::class)
        addPlay(ClientboundSetHealthPacket::class)
        addPlay(ClientboundSetHeldItemPacket::class)
        addPlay(ClientboundScoreboardObjectivePacket::class)
        skipPlay("set passenger")
        addPlay(ClientboundSetInventorySlotPacket::class)
        addPlay(ClientboundTeamsPacket::class)
        addPlay(ClientboundUpdateScorePacket::class)
        skipPlay("update simulation distance")
        addPlay(ClientboundSetSubtitlePacket::class)
        addPlay(ClientboundUpdateTimePacket::class)
        addPlay(ClientboundSetTitlePacket::class)
        addPlay(ClientboundSetTitleTimesPacket::class)
        skipPlay("entity sound effect")
        addPlay(ClientboundPlaySoundPacket::class)
        skipPlay("start configuration")
        skipPlay("stop sound")
        skipPlay("cookie store")
        addPlay(ClientboundSystemChatMessagePacket::class)
        addPlay(ClientboundTabListPacket::class)
        skipPlay("nbt query response")
        skipPlay("collect item")
        addPlay(ClientboundEntityTeleportPacket::class)
        addPlay(ClientboundSetTickingStatePacket::class)
        skipPlay("tick step")
        skipPlay("transfer packet")
        skipPlay("advancements")
        skipPlay("entity attribute")
        addPlay(ClientboundEntityEffectPacket::class)
        skipPlay("declare recipes")
        skipPlay("tags")
        skipPlay("projectile power")
        skipPlay("custom report details")
        skipPlay("server links")
    }

    private fun addHandshake(packet: KClass<*>) {
        handshakePackets[packet] = handshakeCounter.getAndIncrement()
    }

    private fun addStatus(packet: KClass<*>) {
        statusPackets[packet] = statusCounter.getAndIncrement()
    }

    private fun addLogin(packet: KClass<*>) {
        loginPackets[packet] = loginCounter.getAndIncrement()
    }

    private fun addConfiguration(packet: KClass<*>) {
        configurationPackets[packet] = configurationCounter.getAndIncrement()
    }

    private fun addPlay(packet: KClass<*>) {
        playPackets[packet] = playCounter.getAndIncrement()
    }

    private fun skipHandshake(string: String) { handshakeCounter.getAndIncrement() }
    private fun skipStatus(string: String) { statusCounter.getAndIncrement() }
    private fun skipLogin(string: String) { loginCounter.getAndIncrement() }
    private fun skipConfiguration(string: String) { configurationCounter.getAndIncrement() }
    private fun skipPlay(string: String) { playCounter.getAndIncrement() }

}


