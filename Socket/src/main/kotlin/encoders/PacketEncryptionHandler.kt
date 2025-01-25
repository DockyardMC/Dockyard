package io.github.dockyardmc.socket.encoders

import io.github.dockyardmc.socket.cryptography.EncryptionBase
import io.github.dockyardmc.socket.cryptography.EncryptionUtil
import io.github.dockyardmc.socket.cryptography.PlayerCrypto
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class PacketEncryptionHandler(private val playerCrypto: PlayerCrypto) : MessageToByteEncoder<ByteBuf>() {

    private val encryptionBase = EncryptionBase(EncryptionUtil.getEncryptionCipherInstance(playerCrypto))

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {

        if(!playerCrypto.isConnectionEncrypted) {
            out.writeBytes(msg.retain()); return
        }

        out.writeBytes(encryptionBase.encrypt(msg.retain()))
        msg.release()
    }
}