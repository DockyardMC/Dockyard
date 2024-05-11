package io.github.dockyardmc.protocol.encryption

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.player.PlayerConnectionEncryption
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.handler.codec.MessageToMessageDecoder
import log

class PacketDecryptionHandler(private val playerConnectionEncryption: PlayerConnectionEncryption): MessageToMessageDecoder<ByteBuf>() {

    private val encryptionBase = EncryptionBase(EncryptionUtil.getDecryptionCipherInstance(playerConnectionEncryption))
    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        if(!playerConnectionEncryption.isEncrypted) { out.add(msg); return}
        val byteBuf = encryptionBase.decrypt(ctx, msg)
        out.add(byteBuf)
    }
}