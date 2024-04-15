package io.github.dockyardmc.protocol.packets.status

import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerListPingEvent
import io.github.dockyardmc.extentions.byteSize
import io.netty.channel.ChannelHandlerContext
import java.io.File
import java.util.*

class StatusPacketHandler {

    fun handleHandshake(packet: ServerboundHandshakePacket, connection: ChannelHandlerContext) {

        val messageBindable = Bindable("dewfault message")

        Events.dispatch(ServerListPingEvent(messageBindable))

        val json = getServerStatusJson(messageBindable.value, packet.version)
        val out = ClientboundStatusResponsePacket(json.byteSize() + 3, 0, json)

        connection.write(out.asByteBuf())
    }


    private val icon = File("./icon.png")
    val encoded: ByteArray = Base64.getEncoder().encode(icon.readBytes())
    fun getServerStatusJson(message: String, version: Int): String {
        return """
            {
                "version": {
                    "name": "1.20.4",
                    "protocol": $version
                },
                "players": {
                    "max": 420,
                    "online": 69
                },
                "description": "§bDockyardMC §8| §7Kotlin Server Implementation\n§8$message",
                "enforceSecureChat": true,
                "previewsChat": false,
                "favicon": "data:image/png;base64,${encoded.decodeToString()}"
            }
        """.trimIndent()
    }
}