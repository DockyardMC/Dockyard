package io.github.dockyardmc.protocol.packets.login

import LogType
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.encryption.PacketDecryptionHandler
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.encryption.PacketEncryptionHandler
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.handshake.ServerboundHandshakePacket
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

        PlayerManager.add(player, processor)

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

        log("Shared Secret: ${sharedSecret.toHexString()}", LogType.DEBUG)
        log("Verify Token: ${verifyToken.toHexString()} (MATCHES)", LogType.DEBUG)

        processor.player.connectionEncryption.sharedSecret = SecretKeySpec(sharedSecret, "AES")
        processor.player.connectionEncryption.isEncrypted = true
        processor.encrypted = true
        log("Encryption Enabled", LogType.SUCCESS)

        val pipeline = connection.channel().pipeline()
        pipeline.addBefore("processor", "decryptor", PacketDecryptionHandler(processor.player.connectionEncryption))
        pipeline.addBefore("decryptor", "encryptor", PacketEncryptionHandler(processor.player.connectionEncryption))

        val player = processor.player

        val list = mutableListOf<ProfilePropertyMap>()

        val value = "ewogICJ0aW1lc3RhbXAiIDogMTcxNDg1ODE4MzQ4MCwKICAicHJvZmlsZUlkIiA6ICIwYzkxNTFlNDcwODM0MThkYTI5Y2JiYzU4ZjdjNzQxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJMdWt5bmthQ1pFIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhiMDIyNDA4YzVmZGQwMTE0NDJmNDY3ZTU1MWMwMWQ4MmI3NTJmMmZlY2VkYTkwY2FlNGI4MmM2MTIxODYwMzIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
        val signature = "a2M9RWfbNsziP1mNoSeDdIjlaKQ5xDoOGiQmSm58pu/4aBg++WJDkMAN8P28F3i1zoQFAaKro++NK5QuLHAY+4hE3ZSDjSXUNqP0QqWELWcnzmTZaLtqG6DPAnX/kzBY6wrdhDdVf2exnHRuIMYt53fjahHoQEebDYPHYAICKBkvdsU5nHWDa9WGX3W7SD720VQBZs8pbNYA/IilyQdM32OwoW4q+6cSx37/gP+l0ayCy90EtB/PlqSwYc4e1JX6tDIBmRxjL0VMCaE0tBo8M7zG7/Pdu6uXnCcvcn6sVuVlIxHiuUSDSjRimU92eaH6pRg6toP/BdFPO9wjXcvFSKHET4pJ6vsCS2cOMCuUljH2tg1dXuyMuoFRVEpdKFyPU+GBIEoXubGmMebf/eZP6u9rSm0C/1J2FR+R9vcFX0p8nkgWtponfo+srr3D3AQLFOHTZUynmN9ehXdc+ImuDTZHSIkkhLwybIy+d+vypHS4WfJqrPSfuVM29krKfAWV9kN5QRtUIBzpoQlMbU48sf/3NK4xYrjb5TDCGKvvONaHww2YZXDm3Z6KyPx3NwKNlyrweQ+Kj71DByubk0CdJpgfYr1jS2jBcyypnlGhJnC11XDnOZGyBs5oa3w6taKJYwweYZXehqwOkmwnSfb5Fe0Q2dkiynS96y0spRwUSEQ="
        val texturesProperty = ProfileProperty("value", value, true, signature)
        val texturesPropertyMap = ProfilePropertyMap("textures", mutableListOf(texturesProperty))
        list.add(texturesPropertyMap)
        player.profile = texturesPropertyMap

        connection.sendPacket(ClientboundLoginCompressionPacket())
        connection.sendPacket(ClientboundLoginSuccessPacket(player.uuid, player.username, list))
    }

    fun handleLoginAcknowledge(packet: ServerboundLoginAcknowledgedPacket, connection: ChannelHandlerContext) {
        processor.state = ProtocolState.CONFIGURATION
    }
}