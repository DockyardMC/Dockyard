package io.github.dockyardmc.protocol.plugin.messages

import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf
import io.netty.util.AsciiString
import java.nio.charset.StandardCharsets

data class RegisterPluginMessage(val channels: List<String>) : PluginMessage {
    companion object {

        val MOJANG_FUCKED_STRING_LIST_STREAM_CODEC = object : StreamCodec<List<String>> {

            override fun write(buffer: ByteBuf, value: List<String>) {
                var first = true
                value.forEach { channel ->
                    if (first) first = false else buffer.writeByte(0)
                    buffer.writeBytes(channel.toByteArray(StandardCharsets.US_ASCII))
                }
            }

            override fun read(buffer: ByteBuf): List<String> {
                val list: MutableList<String> = mutableListOf()
                var current = StringBuilder()

                while (buffer.isReadable) {
                    val byte: Byte = buffer.readByte()

                    if (byte.toInt() != 0) {
                        current.append(AsciiString.b2c(byte))
                    } else {
                        list.add(current.toString())
                        current = StringBuilder()
                    }
                }
                list.add(current.toString())
                return list
            }
        }

        val STREAM_CODEC = StreamCodec.of(MOJANG_FUCKED_STRING_LIST_STREAM_CODEC, RegisterPluginMessage::channels, ::RegisterPluginMessage)
    }

    override fun getStreamCodec(): StreamCodec<out PluginMessage> {
        return STREAM_CODEC
    }
}