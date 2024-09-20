package io.github.dockyardmc.protocol.plugin.messages

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.RegisterPluginChannelsEvent
import io.github.dockyardmc.events.UnregisterPluginChannelsEvent
import io.github.dockyardmc.player.Player
import io.netty.buffer.ByteBuf
import io.netty.util.AsciiString
import java.nio.charset.StandardCharsets

class UnregisterPluginMessage(val channels: List<String>): PluginMessageHandler() {
    companion object {
        fun read(buf: ByteBuf): RegisterPluginMessage {
            val list: MutableList<String> = mutableListOf()
            var current = StringBuilder()

            while (buf.isReadable) {
                val byte: Byte = buf.readByte()

                if (byte.toInt() != 0) {
                    current.append(AsciiString.b2c(byte))
                } else {
                    list.add(current.toString())
                    current = StringBuilder()
                }
            }
            list.add(current.toString())

            return RegisterPluginMessage(list)
        }
    }

    override fun handle(player: Player) {
        val event = UnregisterPluginChannelsEvent(player, channels)
        Events.dispatch(event)
    }

    override fun write(buffer: ByteBuf) {
        var first = true

        channels.forEach {
            if (first) first = false else buffer.writeByte(0)
            buffer.writeBytes(it.toByteArray(StandardCharsets.US_ASCII))
        }
    }
}