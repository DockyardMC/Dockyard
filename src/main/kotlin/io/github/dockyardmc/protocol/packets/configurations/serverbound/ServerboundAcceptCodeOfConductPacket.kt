package io.github.dockyardmc.protocol.packets.configurations.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerAcceptCodeOfConductEvent
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.channel.ChannelHandlerContext

class ServerboundAcceptCodeOfConductPacket : ServerboundPacket {

    companion object {
        val STREAM_CODEC = StreamCodec.of(::ServerboundAcceptCodeOfConductPacket)
    }

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        Events.dispatch(PlayerAcceptCodeOfConductEvent(processor.player, getPlayerEventContext(processor.player)))
    }

}