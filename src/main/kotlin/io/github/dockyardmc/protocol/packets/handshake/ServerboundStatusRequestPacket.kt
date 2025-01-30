package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerListPingEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.motd.ServerListPlayer
import io.github.dockyardmc.motd.ServerStatusManager
import io.github.dockyardmc.motd.toJson
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundStatusRequestPacket: ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val players = mutableListOf<ServerListPlayer>()
        PlayerManager.players.forEach {
            players.add(ServerListPlayer(it.username, it.uuid.toString()))
        }

        val serverStatus = ServerStatusManager.getCache(processor.joinedThroughIp)
        Events.dispatch(ServerListPingEvent(processor, serverStatus))

        val json = serverStatus.toJson()
        val out = ClientboundStatusResponsePacket(json)

        connection.sendPacket(out, processor)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundStatusRequestPacket {
            return ServerboundStatusRequestPacket()
        }
    }

}