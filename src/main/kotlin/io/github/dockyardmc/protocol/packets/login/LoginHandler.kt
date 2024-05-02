package io.github.dockyardmc.protocol.packets.login

import LogType
import io.github.dockyardmc.DECRYPT
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerLoginEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerConnectionEncryption
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.encryption.PacketDecryptionHandler
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.encryption.PacketEncryptionHandler
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.status.ServerboundHandshakePacket
import io.ktor.util.network.*
import io.netty.channel.ChannelHandlerContext
import log
import java.security.KeyPairGenerator
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@OptIn(ExperimentalStdlibApi::class)
class LoginHandler(var processor: PacketProcessor): PacketHandler(processor) {

    fun handleHandshake(packet: ServerboundHandshakePacket, connection: ChannelHandlerContext) {
        processor.state = ProtocolState.LOGIN
    }

    fun handleLoginStart(packet: ServerboundLoginStartPacket, connection: ChannelHandlerContext) {
        log("Received login start packet with name ${packet.name} and UUID ${packet.uuid}", LogType.DEBUG)

        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(1024)
        val keyPair = generator.generateKeyPair()
        val privateKey = keyPair.private
        val publicKey = keyPair.public

        val secureRandom = SecureRandom()
        val verificationToken  = ByteArray(4)
        secureRandom.nextBytes(verificationToken)
        // verificationToken.size reports 4 but nextBytes WRITES 8 IN REALITY... WHY???? I HAVE SPENT 2 HOURS DEBUGGING THIS

        val playerConnectionEncryption = PlayerConnectionEncryption(publicKey, privateKey, verificationToken)
        val player = Player(packet.name, packet.uuid, connection.channel().remoteAddress().address, playerConnectionEncryption, connection)

        PlayerManager.players.add(player)
        processor.player = player

        Events.dispatch(PlayerLoginEvent(player))

        val out = ClientboundEncryptionRequestPacket("", publicKey.encoded, verificationToken)
        connection.sendPacket(out)
    }

    fun handleEncryptionResponse(packet: ServerboundEncryptionResponsePacket, connection: ChannelHandlerContext) {
        log("Received encryption response: ${packet.sharedSecret.size}bytes | ${packet.verifyToken.size}bytes")

        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, processor.player.connectionEncryption.privateKey)

        val verifyToken = cipher.doFinal(packet.verifyToken)
        val sharedSecret = cipher.doFinal(packet.sharedSecret)

        if(!verifyToken.contentEquals(processor.player.connectionEncryption.verifyToken)) log("Verify Token of player ${processor.player.username} does not match!", LogType.ERROR)

        log("Shared Secret: ${sharedSecret.toHexString()}", DECRYPT)
        log("Verify Token: ${verifyToken.toHexString()} (MATCHES)", DECRYPT)

        processor.player.connectionEncryption.sharedSecret = SecretKeySpec(sharedSecret, "AES")
        processor.encrypted = true
        log("Encryption Enabled", LogType.SUCCESS)

        connection.channel().pipeline().removeLast()
        connection.channel().pipeline().addLast(PacketDecryptionHandler(processor.player.connectionEncryption))
        connection.channel().pipeline().addLast(PacketEncryptionHandler(processor.player.connectionEncryption))
        connection.channel().pipeline().addLast(processor)

        val player = processor.player
        connection.sendPacket(ClientboundLoginSuccessPacket(player.uuid, player.username, mutableListOf()))
      }
}