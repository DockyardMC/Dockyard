package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.toByteArraySafe
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Serverbound Plugin Message (play)")
@ServerboundPacketInfo(0x12, ProtocolState.PLAY)
class ServerboundPlayPluginMessagePacket(val channel: String, val data: ByteArray): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundPlayPluginMessagePacket {
            val channel = byteBuf.readString()
            val data = byteBuf.readRawBytes()
            return ServerboundPlayPluginMessagePacket(channel, data)
        }
    }
}

fun ByteBuf.readRawBytes(): ByteArray {
    val limit = this.nioBuffer().limit()
    val length = limit - this.readerIndex()
    val bytes = ByteArray(length)
    this.nioBuffer().get(this.readerIndex(), bytes)
    this.readerIndex(this.readerIndex() + length)
    return bytes
}