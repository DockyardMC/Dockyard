package io.github.dockyardmc.protocol.packets.login

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.entities.EntityManager
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.*
import io.github.dockyardmc.player.kick.KickReason
import io.github.dockyardmc.player.kick.getSystemKickMessage
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.cryptography.PacketDecryptionHandler
import io.github.dockyardmc.protocol.cryptography.PacketEncryptionHandler
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.handshake.ServerboundHandshakePacket
import io.github.dockyardmc.runnables.AsyncRunnable
import io.github.dockyardmc.utils.MojangUtil
import io.github.dockyardmc.utils.debug
import io.github.dockyardmc.world.WorldManager
import io.ktor.util.network.*
import io.netty.channel.ChannelHandlerContext
import java.security.KeyPairGenerator
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class LoginHandler(var processor: PacketProcessor) : PacketHandler(processor) {

    fun handleHandshake(packet: ServerboundHandshakePacket, connection: ChannelHandlerContext) {
        processor.playerProtocolVersion = packet.version
        processor.state = ProtocolState.LOGIN
    }

    fun handleLoginStart(packet: ServerboundLoginStartPacket, connection: ChannelHandlerContext) {
        val name = packet.name
        val uuid = packet.uuid
        debug("Received login start packet with name $name and UUID $uuid", logType = LogType.DEBUG)

        if (!DockyardServer.allowAnyVersion) {
            val playerVersion = processor.playerProtocolVersion
            val requiredVersion = DockyardServer.minecraftVersion.protocolId
            if (processor.playerProtocolVersion != requiredVersion) {
                connection.sendPacket(
                    ClientboundLoginDisconnectPacket(
                        getSystemKickMessage(
                            "You are using incompatible version <red>($playerVersion)<gray>. Please use version <yellow>${DockyardServer.minecraftVersion.versionName}<gray>",
                            KickReason.INCOMPATIBLE_VERSION.name
                        )
                    ), processor
                )
                return
            }
        }

        val dummyCrypto = PlayerCrypto()
        val player = Player(
            username = name,
            entityId = EntityManager.entityIdCounter.incrementAndGet(),
            uuid = uuid,
            world = WorldManager.mainWorld,
            address = connection.channel().remoteAddress().address,
            crypto = dummyCrypto,
            connection = connection,
        )

        PlayerManager.add(player, processor)
        EntityManager.entities.add(player)

        if (ConfigManager.config.useMojangAuth) {
            val generator = KeyPairGenerator.getInstance("RSA")
            generator.initialize(1024)
            val keyPair = generator.generateKeyPair()
            val privateKey = keyPair.private
            val publicKey = keyPair.public

            val secureRandom = SecureRandom()
            val verificationToken = ByteArray(4)
            secureRandom.nextBytes(verificationToken)

            val playerCrypto = PlayerCrypto(publicKey, privateKey, verificationToken)
            player.crypto = playerCrypto

            // pre-cache the skin
            val asyncRunnable = AsyncRunnable {
                MojangUtil.getSkinFromUUID(player.uuid)
            }
            asyncRunnable.run()
            val out = ClientboundEncryptionRequestPacket("", publicKey.encoded, verificationToken, true)
            connection.sendPacket(out, processor)
        } else {
            finishEncryption(player)
        }
    }

    fun handleEncryptionResponse(packet: ServerboundEncryptionResponsePacket, connection: ChannelHandlerContext) {

        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, processor.player.crypto.privateKey)

        val verifyToken = cipher.doFinal(packet.verifyToken)
        val sharedSecret = cipher.doFinal(packet.sharedSecret)

        if (!verifyToken.contentEquals(processor.player.crypto.verifyToken)) log(
            "Verify Token of player ${processor.player.username} does not match!",
            LogType.ERROR
        )

        processor.player.crypto.sharedSecret = SecretKeySpec(sharedSecret, "AES")
        processor.player.crypto.isConnectionEncrypted = true
        processor.encrypted = true

        val pipeline = connection.channel().pipeline()
        pipeline.addBefore("processor", "decryptor", PacketDecryptionHandler(processor.player.crypto))
        pipeline.addBefore("decryptor", "encryptor", PacketEncryptionHandler(processor.player.crypto))

        val player = processor.player
        finishEncryption(player)
    }

    fun handleLoginAcknowledge(packet: ServerboundLoginAcknowledgedPacket, connection: ChannelHandlerContext) {
        processor.state = ProtocolState.CONFIGURATION
    }

    fun finishEncryption(player: Player) {
        val list = mutableListOf<ProfilePropertyMap>()

        val texturesProperty = ProfileProperty("textures", "", true, "")
        val texturesPropertyMap = ProfilePropertyMap(player.username, mutableListOf(texturesProperty))
        list.add(texturesPropertyMap)
        player.profile = texturesPropertyMap

        player.sendPacket(ClientboundSetCompressionPacket(-1))
        player.sendPacket(ClientboundLoginSuccessPacket(player.uuid, player.username, list))
    }
}