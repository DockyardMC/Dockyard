package io.github.dockyardmc.protocol.packets.play

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerMoveEvent
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityTeleportPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetHeadYawPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUpdateEntityRotationPacket
import io.github.dockyardmc.protocol.packets.play.serverbound.*
import io.github.dockyardmc.utils.vectors.Vector2f
import io.netty.channel.ChannelHandlerContext

class PlayHandler(var processor: PlayerNetworkManager): PacketHandler(processor) {

    fun handleTeleportConfirmation(packet: ServerboundTeleportConfirmationPacket, connection: ChannelHandlerContext) {
    }

    fun handlePlayerPositionAndRotationUpdates(packet: ServerboundSetPlayerPositionPacket, connection: ChannelHandlerContext) {
        val player = processor.player
        this.handlePlayerPositionAndRotationUpdates(Location(packet.vector3d, player.location.yaw, player.location.pitch, player.world), packet.isOnGround, connection)
    }

    fun handlePlayerPositionAndRotationUpdates(packet: ServerboundSetPlayerPositionAndRotationPacket, connection: ChannelHandlerContext) {
        val player = processor.player
        this.handlePlayerPositionAndRotationUpdates(Location(packet.x, packet.y, packet.z, packet.yaw, packet.pitch, player.world), packet.isOnGround, connection)
    }

    fun handlePlayerPositionAndRotationUpdates(packet: ServerboundSetPlayerRotationPacket, connection: ChannelHandlerContext) {
        val player = processor.player
        this.handlePlayerPositionAndRotationUpdates(Location(player.location.x, player.location.y, player.location.z, packet.yaw, packet.pitch, player.world), packet.isOnGround, connection, true)
    }

    fun handlePlayerPositionAndRotationUpdates(location: Location, isOnGround: Boolean, connection: ChannelHandlerContext, isOnlyHeadMovement: Boolean = false) {
        val player = processor.player
        val oldLocation = player.location

        if(oldLocation == location) return

        val event = PlayerMoveEvent(oldLocation, location, player, isOnlyHeadMovement)
        Events.dispatch(event)
        if(event.cancelled) {
            player.teleport(oldLocation)
            return
        }

        player.location = location
        player.isOnGround = isOnGround

        if(isOnlyHeadMovement) {
            val packet = ClientboundUpdateEntityRotationPacket(player, Vector2f(player.location.yaw, player.location.pitch))
            player.sendPacketToViewers(packet)
        } else {
            val packet = ClientboundEntityTeleportPacket(player, oldLocation)
            player.sendPacketToViewers(packet)
        }
        val headRotPacket = ClientboundSetHeadYawPacket(player)
        player.sendPacketToViewers(headRotPacket)

        player.chunkViewSystem.update()
    }

    fun handleKeepAlive(packet: ServerboundKeepAlivePacket, connection: ChannelHandlerContext) {
        processor.respondedToLastKeepAlive = true
    }
}