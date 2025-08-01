package io.github.dockyardmc.protocol.packets.configurations

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.readUUID
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.resourcepack.ResourcePack
import io.github.dockyardmc.resourcepack.ResourcepackManager
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import java.util.*

data class ServerboundConfigurationResourcepackResponsePacket(var uuid: UUID, var response: ResourcePack.Status) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        log("$uuid $player $response", LogType.FATAL)
        ResourcepackManager.handleResponse(player, uuid, response)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundConfigurationResourcepackResponsePacket {
            return ServerboundConfigurationResourcepackResponsePacket(buffer.readUUID(), buffer.readEnum<ResourcePack.Status>())
        }
    }
}