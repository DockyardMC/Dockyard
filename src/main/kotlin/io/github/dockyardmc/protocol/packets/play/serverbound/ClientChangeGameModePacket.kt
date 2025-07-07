package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

data class ClientChangeGameModePacket(val gameMode: GameMode) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        if (processor.player.hasPermission("dockyard.commands.gamemode")) {
            player.gameMode.value = gameMode
        }
    }

    companion object : NetworkReadable<ClientChangeGameModePacket> {

        override fun read(buffer: ByteBuf): ClientChangeGameModePacket {
            return ClientChangeGameModePacket(buffer.readEnum())
        }

    }
}