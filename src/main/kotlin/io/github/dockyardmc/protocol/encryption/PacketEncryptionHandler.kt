package io.github.dockyardmc.protocol.encryption

import io.github.dockyardmc.extentions.toByteArraySafe
import io.github.dockyardmc.player.PlayerConnectionEncryption
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import log

class PacketEncryptionHandler(playerConnectionEncryption: PlayerConnectionEncryption): MessageToMessageEncoder<ByteBuf>() {

    private val encryptionBase = EncryptionBase(EncryptionUtil.getEncryptionCipherInstance(playerConnectionEncryption))

    @OptIn(ExperimentalStdlibApi::class)
    override fun encode(ctx: ChannelHandlerContext?, msg: ByteBuf, out: MutableList<Any>) {
        val packet = encryptionBase.encrypt(msg)
        log("ENCRYPTING PACKET ENCRYPTING PACKET")
        log(packet.toByteArraySafe().toHexString())

        out.add(encryptionBase.encrypt(msg))
    }
}