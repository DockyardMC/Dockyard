package io.github.dockyardmc.protocol.packets.login

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.extentions.broadcastMessage
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
import io.github.dockyardmc.protocol.proxy.LegacyBungeeCordProxySupport
import io.github.dockyardmc.protocol.types.GameProfile
import io.github.dockyardmc.registry.registries.MinecraftVersionRegistry
import io.github.dockyardmc.utils.MojangUtil
import io.github.dockyardmc.utils.isValidMinecraftUsername
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
        const val INVALID_BUNGEECORD_FORWARDING = "Invalid connection, please connect through the BungeeCord proxy. If you believe this is an error, contact a server administrator."

        val uuidRegex = "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex()
    }

    private val crypto = EncryptionUtil.getNewPlayerCrypto()
    private var packetUsername: String? = null

    fun handleHandshake(packet: ServerboundHandshakePacket, connection: ChannelHandlerContext) {

        var address = packet.serverAddress
        val playerVersion = MinecraftVersionRegistry.getOrNull(packet.version)?.versionName ?: "unknown"
        val requiredVersion = DockyardServer.minecraftVersion.protocolId
        if (packet.version != requiredVersion) {
            networkManager.kick("You are using incompatible minecraft version <red>($playerVersion)<gray>. Please use <yellow>${DockyardServer.minecraftVersion.versionName}<gray>", connection)
            return
        }

        networkManager.playerProtocolVersion = packet.version
        if (packet.intent == ServerboundHandshakePacket.Intent.LOGIN) {
            networkManager.state = ProtocolState.LOGIN

            if (LegacyBungeeCordProxySupport.enabled) {
                val split = address.split("\u0000")

                if (split.size == 3 || split.size == 4) {
                    val hasProperties = split.size == 4
                    if (LegacyBungeeCordProxySupport.isBungeeGuardEnabled() && !hasProperties) {
                        networkManager.kick(INVALID_BUNGEECORD_FORWARDING, connection)
                        return
                    }

                    address = split[0]
                    val uuid = UUID.fromString(
                        split[2].replaceFirst(
                            "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)".toRegex(), "$1-$2-$3-$4-$5"
                        )
                    )

                    val properties = mutableListOf<GameProfile.Property>()
                    if (hasProperties) {
                        val foundBungeeGuardToken = false
                        val rawPropertyJson = split[3]
                        broadcastMessage(rawPropertyJson)
//                        val json = Json.decodeFromString<List<GameProfile.Property>>(rawPropertyJson)
                    }
                }
            }
        }
    }

    fun handleLoginStart(packet: ServerboundLoginStartPacket, connection: ChannelHandlerContext) {
        if (!isValidMinecraftUsername(packet.name)) {
            networkManager.kick(ERROR_INVALID_USERNAME, connection)
            return
        }

        packetUsername = packet.name

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

            val gameProfile: GameProfile = if (LegacyBungeeCordProxySupport.enabled) {
                GameProfile(uuid, packet.name)
            } else {
                GameProfile(uuid, packet.name)
            }
            startConfigurationPhase(connection, gameProfile)
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

            val uuid = UUID.fromString(profileResponse.id.replaceFirst(uuidRegex, "$1-$2-$3-$4-$5"))
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

            player.sendPacket(ClientboundSetCompressionPacket(NetworkCompression.compressionThreshold))

            if (NetworkCompression.compressionThreshold > -1) {
                val pipeline = connection.channel().pipeline()
                pipeline.addBefore(ChannelHandlers.RAW_PACKET_DECODER, ChannelHandlers.PACKET_COMPRESSION_DECODER, CompressionDecoder(player.networkManager))
                pipeline.addBefore(ChannelHandlers.RAW_PACKET_ENCODER, ChannelHandlers.PACKET_COMPRESSION_ENCODER, CompressionEncoder(player.networkManager))
            }

            player.sendPacket(ClientboundLoginSuccessPacket(player.uuid, player.username, gameProfile))
            ConfigurationHandler.enterConfiguration(player, connection, true)
        }
    }
}