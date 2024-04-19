package io.github.dockyardmc.protocol.packets.login

import LogType
import io.github.dockyardmc.PacketProcessor
import io.github.dockyardmc.TEMP
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerConnectionEncryption
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.status.ServerboundHandshakePacket
import io.netty.channel.ChannelHandlerContext
import log
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import javax.crypto.Cipher

@OptIn(ExperimentalStdlibApi::class)
class LoginHandler(var processor: PacketProcessor): PacketHandler(processor) {

    private lateinit var player: Player

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
        val player = Player(packet.name, packet.uuid, playerConnectionEncryption)

        PlayerManager.players.add(player)

        this.player = player

        val out = ClientboundEncryptionRequestPacket("", publicKey.encoded, verificationToken)
        connection.write(out.asByteBuf())
    }

    fun handleEncryptionResponse(packet: ServerboundEncryptionResponsePacket, connection: ChannelHandlerContext) {
        log("Received encryption response: ${packet.sharedSecret.size} | ${packet.verifyToken.size}")

        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, player.connectionEncryption.privateKey)

        val verifyToken = cipher.doFinal(packet.verifyToken)

        log(player.connectionEncryption.verifyToken.toHexString(HexFormat.Default))
        log(verifyToken.toHexString(HexFormat.Default), TEMP)
    }
}

