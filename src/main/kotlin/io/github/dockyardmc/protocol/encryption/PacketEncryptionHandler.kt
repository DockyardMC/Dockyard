package io.github.dockyardmc.protocol.encryption

import io.github.dockyardmc.player.PlayerConnectionEncryption
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class PacketEncryptionHandler(private val playerConnectionEncryption: PlayerConnectionEncryption): MessageToByteEncoder<ByteBuf>() {

    private val encryptionBase = EncryptionBase(EncryptionUtil.getEncryptionCipherInstance(playerConnectionEncryption))
    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        if(!playerConnectionEncryption.isEncrypted) { out.writeBytes(msg); return}

        encryptionBase.encrypt(msg, out)
    }
}