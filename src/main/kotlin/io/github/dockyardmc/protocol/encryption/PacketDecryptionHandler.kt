package io.github.dockyardmc.protocol.encryption

import io.github.dockyardmc.DECRYPT
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.player.PlayerConnectionEncryption
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.handler.codec.MessageToMessageDecoder
import log

class PacketDecryptionHandler(playerConnectionEncryption: PlayerConnectionEncryption): MessageToMessageDecoder<ByteBuf>() {

    private val encryptionBase = EncryptionBase(EncryptionUtil.getDecryptionCipherInstance(playerConnectionEncryption))

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        log("Decrypting packet..", DECRYPT)
        val size = msg.readVarInt()
        val id = msg.readVarInt()

        log("Size: $size", DECRYPT)
        log("Id: $id", DECRYPT)

        val byteBuf = encryptionBase.decrypt(ctx, msg)
        out.add(byteBuf)
    }
}