package io.github.dockyardmc.protocol.packets.login

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.PlayerManager
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
import io.github.dockyardmc.protocol.plugin.LoginPluginMessageHandler
import io.github.dockyardmc.protocol.proxy.VelocityProxy
import io.github.dockyardmc.protocol.types.GameProfile
import io.github.dockyardmc.registry.registries.MinecraftVersionRegistry
import io.github.dockyardmc.utils.MojangUtil
import io.github.dockyardmc.utils.isValidMinecraftUsername
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.util.*
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
        const val ERROR_PROXY_VELOCITY_NULL_RESPONSE = "<yellow>Response from Velocity is null\n\n<gray>(are you connecting through a proxy?)\n(make sure to set your <#ededed>player-info-forwarding-mode<gray> to <#dbdbdb>\"modern\"<gray>)"
        const val ERROR_PROXY_VELOCITY_INTEGRITY = "Failed Integrity check with Velocity"

        val UUID_REGEX = "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex()
    }

    private val crypto = EncryptionUtil.getNewPlayerCrypto()
    private var packetUsername: String? = null

    fun handleHandshake(packet: ServerboundHandshakePacket, connection: ChannelHandlerContext) {

        val playerVersion = MinecraftVersionRegistry.getOrNull(packet.version)?.versionName ?: "unknown"
        val requiredVersion = DockyardServer.minecraftVersion.protocolId
        if (!DockyardServer.allowAnyVersion && packet.version != requiredVersion) {
            networkManager.kick("You are using incompatible minecraft version <red>($playerVersion)<gray>. Please use <yellow>${DockyardServer.minecraftVersion.versionName}<gray>", connection)
            return
        }

        networkManager.playerProtocolVersion = packet.version
        if (packet.intent == ServerboundHandshakePacket.Intent.LOGIN) {
            networkManager.state = ProtocolState.LOGIN
        }
    }

    fun handleLoginStart(packet: ServerboundLoginStartPacket, connection: ChannelHandlerContext) {
        if (!isValidMinecraftUsername(packet.name)) {
            networkManager.kick(ERROR_INVALID_USERNAME, connection)
            return
        }

        packetUsername = packet.name

        if (VelocityProxy.enabled) {
            log("Requesting velocity game profile..", LogType.SECURITY)
            networkManager.loginPluginMessageHandler.request(connection, VelocityProxy.PLAYER_INFO_CHANNEL, Unpooled.buffer()).thenAccept { response ->
                handleVelocityResponse(response, connection)
            }
            return
        }

        if (ConfigManager.config.useMojangAuth) {
            connection.sendPacket(
                ClientboundEncryptionRequestPacket(
                    "",
                    crypto.publicKey.encoded,
                    crypto.verifyToken,
                    true
                ),
                networkManager
            )
        } else {
            val uuid = UUID.nameUUIDFromBytes("OfflinePlayer:${packet.name}".toByteArray(StandardCharsets.UTF_8));
            startConfigurationPhase(connection, GameProfile(uuid, packet.name))
        }
    }

    fun handleVelocityResponse(response: LoginPluginMessageHandler.Response, connection: ChannelHandlerContext) {
        log("Received response from velocity", LogType.SECURITY)
        try {
            val buffer = response.responsePayload
            if (buffer == null || buffer.readableBytes() == 0) {
                networkManager.kick(ERROR_PROXY_VELOCITY_NULL_RESPONSE, connection)
                return
            }

            val integrity = VelocityProxy.checkIntegrity(buffer)
            if (!integrity) {
                networkManager.kick(ERROR_PROXY_VELOCITY_INTEGRITY, connection)
                return
            }

            val actualAddress = buffer.readString()
            val gameProfile = GameProfile.read(buffer)
            networkManager.address = actualAddress
            startConfigurationPhase(connection, gameProfile)
        } catch (exception: Exception) {
            log("Error happened during authenticating with velocity proxy:", LogType.ERROR)
            log(exception)
        }
    }

    fun handleEncryptionResponse(packet: ServerboundEncryptionResponsePacket, connection: ChannelHandlerContext) {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, crypto.privateKey)

        val verifyToken = cipher.doFinal(packet.verifyToken)
        val sharedSecret = cipher.doFinal(packet.sharedSecret)

        if (!verifyToken.contentEquals(crypto.verifyToken)) {
            log("Verify Token of player ${networkManager.player.username} does not match!", LogType.ERROR)
            networkManager.kick(ERROR_VERIFY_TOKEN_DOES_NOT_MATCH, connection)
            return
        }

        val sharedSecretKey = SecretKeySpec(sharedSecret, "AES")
        val digestedData = EncryptionUtil.digestData("", EncryptionUtil.keyPair.public, sharedSecretKey)
        if (digestedData == null) {
            log("Failed to initialize encryption for $packetUsername", LogType.ERROR)
            networkManager.kick(ERROR_VERIFY_TOKEN_DOES_NOT_MATCH, connection)
            return
        }

        val serverId = BigInteger(digestedData).toString(16)
        val username = checkNotNull(packetUsername) { "Player name is not initialized" }

        val profile = try {
            val profileResponse = MojangUtil.authenticateSession(username, serverId)

            val uuid = UUID.fromString(profileResponse.id.replaceFirst(UUID_REGEX, "$1-$2-$3-$4-$5"))
            val name = profileResponse.name
            val properties = profileResponse.properties.toMutableList()

            GameProfile(uuid, name, properties)
        } catch (ex: Exception) {
            log(ex)
            networkManager.kick(ERROR_SESSION_SERVERS, connection)
            return
        }

        crypto.sharedSecret = sharedSecretKey
        crypto.isConnectionEncrypted = true
        networkManager.encryptionEnabled = true

        connection.channel().pipeline()
            .addBefore(ChannelHandlers.PACKET_LENGTH_DECODER, ChannelHandlers.PACKET_DECRYPTOR, PacketDecryptionHandler(crypto))
            .addBefore(ChannelHandlers.PACKET_LENGTH_ENCODER, ChannelHandlers.PACKET_ENCRYPTOR, PacketEncryptionHandler(crypto))

        startConfigurationPhase(connection, profile)
    }

    fun handleLoginAcknowledge(packet: ServerboundLoginAcknowledgedPacket, connection: ChannelHandlerContext) {
        networkManager.state = ProtocolState.CONFIGURATION
    }

    fun startConfigurationPhase(connection: ChannelHandlerContext, gameProfile: GameProfile) {
        Thread.startVirtualThread {
            if (PlayerManager.getPlayerByUUIDOrNull(gameProfile.uuid) != null) {
                networkManager.kick(ERROR_ALREADY_CONNECTED, connection)
                return@startVirtualThread
            }

            val player = PlayerManager.createNewPlayer(gameProfile.username, gameProfile.uuid, connection, networkManager)
            player.gameProfile = gameProfile

            player.sendPacket(ClientboundSetCompressionPacket(NetworkCompression.COMPRESSION_THRESHOLD))

            if (NetworkCompression.COMPRESSION_THRESHOLD > -1) {
                val pipeline = connection.channel().pipeline()
                pipeline.addBefore(ChannelHandlers.RAW_PACKET_DECODER, ChannelHandlers.PACKET_COMPRESSION_DECODER, CompressionDecoder(player.networkManager))
                pipeline.addBefore(ChannelHandlers.RAW_PACKET_ENCODER, ChannelHandlers.PACKET_COMPRESSION_ENCODER, CompressionEncoder(player.networkManager))
            }

            player.sendPacket(ClientboundLoginSuccessPacket(player.uuid, player.username, gameProfile))
            ConfigurationHandler.enterConfiguration(player, connection, true)
        }
    }
}
