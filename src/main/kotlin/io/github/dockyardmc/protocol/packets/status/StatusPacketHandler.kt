package io.github.dockyardmc.protocol.packets.status

import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerListPingEvent
import io.github.dockyardmc.extentions.byteSize
import io.github.dockyardmc.motd.Players
import io.github.dockyardmc.motd.ServerStatus
import io.github.dockyardmc.motd.Version
import io.github.dockyardmc.motd.toJson
import io.netty.channel.ChannelHandlerContext
import java.io.File
import java.util.*

class StatusPacketHandler {

    fun handleHandshake(packet: ServerboundHandshakePacket, connection: ChannelHandlerContext) {

        val base64EncodedIcon = Base64.getEncoder().encode(File("./icon.png").readBytes()).decodeToString()

        val serverStatus = ServerStatus(
            version = Version(
                name = "1.20.4",
                protocol = packet.version,
            ),
            players = Players(
                max = 727,
                online = 0,
                sample = mutableListOf(),
            ),
            description = "§bDockyardMC §8| §7Kotlin Server Implementation",
            enforceSecureChat = false,
            previewsChat = false,
            favicon = base64EncodedIcon
        )

        val bindableServerStatus = Bindable<ServerStatus>(serverStatus)

        Events.dispatch(ServerListPingEvent(bindableServerStatus))

        val json = bindableServerStatus.value.toJson()
        val out = ClientboundStatusResponsePacket(json.byteSize() + 3, 0, json)

        connection.write(out.asByteBuf())
    }
}