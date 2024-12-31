package io.github.dockyardmc.integrations.noxesium.packets

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.plugin.messages.PluginMessageHandler
import io.netty.buffer.ByteBuf

data class ClientboundNoxesiumServerInformationPacket(val protocolVersion: Int): PluginMessageHandler(CHANNEL) {

    override fun handle(player: Player) {
        // clientbound
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(protocolVersion)
    }

    companion object {
        const val CHANNEL = "noxesium-v2:server_info"

        fun read(buffer: ByteBuf): ClientboundNoxesiumServerInformationPacket {
            return ClientboundNoxesiumServerInformationPacket(buffer.readVarInt())
        }
    }

}