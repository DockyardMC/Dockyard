package io.github.dockyardmc.server.via

import com.viaversion.viaversion.ViaAPIBase
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import io.github.dockyardmc.player.Player
import io.netty.buffer.ByteBuf

class DockyardViaApi: ViaAPIBase<Player>() {

    override fun getPlayerProtocolVersion(player: Player): ProtocolVersion {
        return ProtocolVersion.getProtocol(player.networkManager.playerProtocolVersion)
    }

    override fun sendRawPacket(player: Player, buffer: ByteBuf) {
        sendRawPacket(player.uuid, buffer)
    }

}