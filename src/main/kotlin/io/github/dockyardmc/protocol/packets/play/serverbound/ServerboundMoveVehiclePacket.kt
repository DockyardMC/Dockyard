package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerSteerVehicleEvent
import io.github.dockyardmc.location.Point
import io.github.dockyardmc.location.readPoint
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundMoveVehiclePacket(var point: Point, var onGround: Boolean): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        if(player.vehicle == null) return

        val playerContext = mutableSetOf<Player>(player)
        if(player.vehicle is Player) playerContext.add(player.vehicle!! as Player)

        val event = PlayerSteerVehicleEvent(player, player.vehicle!!, point.toLocation(player.vehicle!!.world), Event.Context(
            playerContext,
            setOf(player, player.vehicle!!),
            setOf(player.world),
            setOf(player.location, player.vehicle!!.location)
        ))

        Events.dispatch(event)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundMoveVehiclePacket {
            return ServerboundMoveVehiclePacket(buffer.readPoint(), buffer.readBoolean())
        }
    }

}