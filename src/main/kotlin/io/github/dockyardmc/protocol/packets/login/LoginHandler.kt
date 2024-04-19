package io.github.dockyardmc.protocol.packets.login

import LogType
import io.github.dockyardmc.TEMP
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerLoginEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerConnectionEncryption
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.status.ServerboundHandshakePacket
import io.ktor.util.network.*
import io.netty.channel.ChannelHandlerContext
import log
import java.security.KeyPairGenerator
import java.security.SecureRandom
import javax.crypto.Cipher

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
        var privateKey = keyPair.private
        val publicKey = keyPair.public

        val secureRandom = SecureRandom()
        val verificationToken  = ByteArray(4)
        secureRandom.nextBytes(verificationToken)
        // verificationToken.size reports 4 but nextBytes WRITES 8 IN REALITY... WHY???? I HAVE SPENT 2 HOURS DEBUGGING THIS

        val playerConnectionEncryption = PlayerConnectionEncryption(keyPair.public, keyPair.private, verificationToken)
        val player = Player(packet.name, packet.uuid, connection.channel().remoteAddress().address, playerConnectionEncryption, connection)

        PlayerManager.players.add(player)
        processor.player = player

        Events.dispatch(PlayerLoginEvent(player))

        processor.encrypted = true

        val out = ClientboundEncryptionRequestPacket("", publicKey.encoded, verificationToken)
        connection.sendPacket(out)
    }

    fun handleEncryptionResponse(packet: ServerboundEncryptionResponsePacket, connection: ChannelHandlerContext) {
        log("Received encryption response: ${packet.sharedSecret.size} | ${packet.verifyToken.size}")

        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, processor.player.connectionEncryption.privateKey)

        val verifyToken = cipher.doFinal(packet.verifyToken)
//        val sharedSecret = cipher.doFinal(packet.sharedSecret)
        val sharedSecret = cipher.doFinal(packet.sharedSecret)

        log(processor.player.connectionEncryption.verifyToken.toHexString(HexFormat.Default))
        log(verifyToken.toHexString(HexFormat.Default), TEMP)
        log(sharedSecret.size.toString(), TEMP)

        val out = ClientboundLoginSuccessPacket(processor.player.uuid, processor.player.username)
        connection.sendPacket(out)
    }
}