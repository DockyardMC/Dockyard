package io.github.dockyardmc.protocol.cryptography

import io.github.dockyardmc.player.PlayerCrypto
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.handler.codec.MessageToMessageDecoder

class PacketDecryptionHandler(private val playerCrypto: PlayerCrypto): MessageToMessageDecoder<ByteBuf>() {

    private val encryptionBase = EncryptionBase(EncryptionUtil.getDecryptionCipherInstance(playerCrypto))
    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        if(!playerCrypto.isConnectionEncrypted) { out.add(msg); return}
        val byteBuf = encryptionBase.decrypt(ctx, msg)
        out.add(byteBuf)
    }
}