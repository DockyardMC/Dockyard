package io.github.dockyardmc.protocol.cryptography

import LogType
import io.github.dockyardmc.player.PlayerCrypto
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.handler.codec.MessageToMessageDecoder
import log


class PacketDecryptionHandler(private val playerCrypto: PlayerCrypto): MessageToMessageDecoder<ByteBuf>() {

    private val encryptionBase = EncryptionBase(EncryptionUtil.getDecryptionCipherInstance(playerCrypto))

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        log("Decrypting stage: buf ref count ${msg.refCnt()}", LogType.TRACE)
        if (!playerCrypto.isConnectionEncrypted) {
            out.add(msg.retain())
            return
        }
        val byteBuf = encryptionBase.decrypt(ctx, msg.retain())
        out.add(byteBuf)
        log("Added to output: buf ref count ${msg.refCnt()}", LogType.TRACE)
        msg.release() // Release msg after processing
        log("Released after decode(): buf ref count ${msg.refCnt()}", LogType.TRACE)
    }
}