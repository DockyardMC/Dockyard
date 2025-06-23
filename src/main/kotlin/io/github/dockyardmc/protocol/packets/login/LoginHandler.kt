package io.github.dockyardmc.protocol.packets.login

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.player.ProfileProperty
import io.github.dockyardmc.player.ProfilePropertyMap
import io.github.dockyardmc.protocol.ChannelHandlers
import io.github.dockyardmc.protocol.NetworkCompression
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.cryptography.EncryptionUtil
import io.github.dockyardmc.protocol.decoders.CompressionDecoder
import io.github.dockyardmc.protocol.decoders.PacketDecryptionHandler
import io.github.dockyardmc.protocol.encoders.CompressionEncoder
import io.github.dockyardmc.protocol.encoders.PacketEncryptionHandler
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.configurations.ConfigurationHandler
import io.github.dockyardmc.protocol.packets.handshake.ServerboundHandshakePacket
import io.github.dockyardmc.registry.registries.MinecraftVersionRegistry
import io.github.dockyardmc.utils.MojangUtil
import io.github.dockyardmc.utils.isValidMinecraftUsername
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.CompletableFuture
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class LoginHandler(var networkManager: PlayerNetworkManager) : PacketHandler(networkManager) {

    private companion object {
        const val ERROR_ALREADY_CONNECTED = "You are already connected to this server!"
        const val ERROR_DURING_LOGIN = "An error occurred during the login phase!"
        const val ERROR_INVALID_USERNAME = "You are connecting with an invalid username!"
        const val ERROR_SESSION_SERVERS = "Failed to contact Mojang's Session Servers (Are they down?)"
        const val ERROR_INVALID_PROXY_RESPONSE = "Invalid proxy response!"
        const val ERROR_VERIFY_TOKEN_DOES_NOT_MATCH = "Your encryption verify token does not match!"
    }

    fun handleHandshake(packet: ServerboundHandshakePacket, connection: ChannelHandlerContext) {

        val playerVersion = MinecraftVersionRegistry.getOrNull(packet.version)?.versionName ?: "unknown"
        val requiredVersion = DockyardServer.minecraftVersion.protocolId
        if (packet.version != requiredVersion) {
            networkManager.kick("You are using incompatible minecraft version <red>($playerVersion)<gray>. Please use <yellow>${DockyardServer.minecraftVersion.versionName}<gray>", connection)
            return
        }

        networkManager.playerProtocolVersion = packet.version
        networkManager.state = ProtocolState.LOGIN
    }

    fun handleLoginStart(packet: ServerboundLoginStartPacket, connection: ChannelHandlerContext) {
        val username = packet.name
        val uuid = packet.uuid

        if(PlayerManager.getPlayerByUsernameOrNull(username) != null) {
            networkManager.kick(ERROR_ALREADY_CONNECTED, connection)
            return
        }

        if(PlayerManager.getPlayerByUUIDOrNull(uuid) != null) {
            networkManager.kick(ERROR_ALREADY_CONNECTED, connection)
            return
        }

        if(!isValidMinecraftUsername(username)) {
            networkManager.kick(ERROR_INVALID_USERNAME, connection)
            return
        }

        val player = PlayerManager.createNewPlayer(username, uuid, connection, networkManager)

        if (ConfigManager.config.useMojangAuth) {
            val crypto = EncryptionUtil.getNewPlayerCrypto()

            player.crypto = crypto

            CompletableFuture.runAsync { MojangUtil.getSkinFromUUID(uuid) }

            val encryptionRequest = ClientboundEncryptionRequestPacket("", crypto.publicKey.encoded, crypto.verifyToken, true)
            connection.sendPacket(encryptionRequest, networkManager)
        } else {
            startConfigurationPhase(player, connection)
        }
    }

    fun handleEncryptionResponse(packet: ServerboundEncryptionResponsePacket, connection: ChannelHandlerContext) {

        val crypto = networkManager.player.crypto!!

        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, crypto.privateKey)

        val verifyToken = cipher.doFinal(packet.verifyToken)
        val sharedSecret = cipher.doFinal(packet.sharedSecret)

        if (!verifyToken.contentEquals(crypto.verifyToken)) {
            log("Verify Token of player ${networkManager.player.username} does not match!", LogType.ERROR)
            networkManager.kick(ERROR_VERIFY_TOKEN_DOES_NOT_MATCH, connection)
            return
        }

        crypto.sharedSecret = SecretKeySpec(sharedSecret, "AES")
        crypto.isConnectionEncrypted = true
        networkManager.encryptionEnabled = true

        val pipeline = connection.channel().pipeline()
        pipeline.addBefore(ChannelHandlers.PACKET_LENGTH_DECODER, ChannelHandlers.PACKET_DECRYPTOR, PacketDecryptionHandler(crypto))
        pipeline.addBefore(ChannelHandlers.PACKET_LENGTH_ENCODER, ChannelHandlers.PACKET_ENCRYPTOR, PacketEncryptionHandler(crypto))

        val player = networkManager.player
        startConfigurationPhase(player, connection)
    }

    fun handleLoginAcknowledge(packet: ServerboundLoginAcknowledgedPacket, connection: ChannelHandlerContext) {
        networkManager.state = ProtocolState.CONFIGURATION
    }

    fun startConfigurationPhase(player: Player, connection: ChannelHandlerContext) {
        val list = mutableListOf<ProfilePropertyMap>()

        val texturesProperty = ProfileProperty("textures", "", true, "")
        val texturesPropertyMap = ProfilePropertyMap(player.username, mutableListOf(texturesProperty))
        list.add(texturesPropertyMap)
        player.profile = texturesPropertyMap

        player.sendPacket(ClientboundSetCompressionPacket(NetworkCompression.compressionThreshold))

        if(NetworkCompression.compressionThreshold > -1) {
            val pipeline = connection.channel().pipeline()
            pipeline.addBefore(ChannelHandlers.RAW_PACKET_DECODER, ChannelHandlers.PACKET_COMPRESSION_DECODER, CompressionDecoder(player.networkManager))
            pipeline.addBefore(ChannelHandlers.RAW_PACKET_ENCODER, ChannelHandlers.PACKET_COMPRESSION_ENCODER, CompressionEncoder(player.networkManager))
        }

        player.sendPacket(ClientboundLoginSuccessPacket(player.uuid, player.username, list))
        ConfigurationHandler.enterConfiguration(player, connection, true)
    }
}