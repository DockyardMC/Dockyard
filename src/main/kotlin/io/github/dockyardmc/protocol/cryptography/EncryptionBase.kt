package io.github.dockyardmc.protocol.cryptography

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import log
import javax.crypto.Cipher

class EncryptionBase(private val cipher: Cipher) {
    private var inputBuffer = ByteArray(0)
    private var outputBuffer = ByteArray(0)

    private fun nettyBufToByteArray(byteBuf: ByteBuf): ByteArray {
        val readableBytes = byteBuf.readableBytes()
        if (inputBuffer.size < readableBytes) {
            inputBuffer = ByteArray(readableBytes)
        }
        byteBuf.readBytes(inputBuffer, 0, readableBytes)
        return inputBuffer
    }

    fun decrypt(channelHandlerContext: ChannelHandlerContext, buf: ByteBuf): ByteBuf {
        val readableBytes = buf.readableBytes()
        val byteArray = nettyBufToByteArray(buf)
        val outputBuf = channelHandlerContext.alloc().heapBuffer(cipher.getOutputSize(readableBytes)).asByteBuf()
        outputBuf.writerIndex(cipher.update(byteArray, 0, readableBytes, outputBuf.array(), outputBuf.arrayOffset()))
        return outputBuf
    }

    fun encrypt(buf: ByteBuf, out: ByteBuf) {
        val i = buf.readableBytes()
        val byteArray = nettyBufToByteArray(buf)
        val outputSize = cipher.getOutputSize(i)
        if (outputBuffer.size < outputSize) {
            outputBuffer = ByteArray(outputSize)
        }
        out.writeBytes(outputBuffer, 0, cipher.update(byteArray, 0, i, outputBuffer))
    }
}
