package io.github.dockyardmc.socket.decoders

import io.github.dockyardmc.socket.cryptography.EncryptionBase
import io.github.dockyardmc.socket.cryptography.EncryptionUtil
import io.github.dockyardmc.socket.cryptography.PlayerCrypto
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.handler.codec.MessageToMessageDecoder


class PacketDecryptionHandler(private val playerCrypto: PlayerCrypto): MessageToMessageDecoder<ByteBuf>() {

    private val encryptionBase = EncryptionBase(EncryptionUtil.getDecryptionCipherInstance(playerCrypto))

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        if (!playerCrypto.isConnectionEncrypted) {
            out.add(msg.retain())
            return
        }
        val byteBuf = encryptionBase.decrypt(ctx, msg.retain())
        out.add(byteBuf)
        msg.release()
    }
}