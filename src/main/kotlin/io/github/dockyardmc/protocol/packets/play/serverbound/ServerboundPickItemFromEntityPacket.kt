package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerPickItemFromEntityEvent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPickItemFromEntityPacket(val entityId: Int, val includeData: Boolean) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val entity = EntityManager.getByIdOrNull(entityId) ?: return

        val context = Event.Context(players = setOf(processor.player), entities = setOf(entity))

        val event = PlayerPickItemFromEntityEvent(processor.player, entity, includeData, context)
        Events.dispatch(event)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundPickItemFromEntityPacket {
            return ServerboundPickItemFromEntityPacket(buffer.readVarInt(), buffer.readBoolean())
        }
    }
}