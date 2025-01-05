package io.github.dockyardmc.protocol.decoders

import io.github.dockyardmc.player.PlayerCrypto
import io.github.dockyardmc.protocol.cryptography.EncryptionBase
import io.github.dockyardmc.protocol.cryptography.EncryptionUtil
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