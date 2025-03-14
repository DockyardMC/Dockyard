package io.github.dockyardmc.protocol.cryptography

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import javax.crypto.Cipher

class EncryptionBase(private val cipher: Cipher) {
    private var inputBuffer = ByteArray(0)
    private var outputBuffer = ByteArray(0)

    fun ByteBuf.toByteArray(): ByteArray {
        val readableBytes = this.readableBytes()
        if (inputBuffer.size < readableBytes) {
            inputBuffer = ByteArray(readableBytes)
        }
        this.readBytes(inputBuffer, 0, readableBytes)
        return inputBuffer

    }
    fun decrypt(connection: ChannelHandlerContext, buffer: ByteBuf): ByteBuf {
        val readableBytes = buffer.readableBytes()
        val byteArray = buffer.toByteArray()
        val outputBuffer = connection.alloc().heapBuffer(cipher.getOutputSize(readableBytes)).asByteBuf()
        outputBuffer.writerIndex(cipher.update(byteArray, 0, readableBytes, outputBuffer.array(), outputBuffer.arrayOffset()))
        return outputBuffer
    }

    fun encrypt(buffer: ByteBuf): ByteBuf {
        val i = buffer.readableBytes()
        val byteArray = buffer.toByteArray()
        val outputSize = cipher.getOutputSize(i)
        if (outputBuffer.size < outputSize) {
            outputBuffer = ByteArray(outputSize)
        }
        return Unpooled.buffer().writeBytes(outputBuffer, 0, cipher.update(byteArray, 0, i, outputBuffer))
    }
}
