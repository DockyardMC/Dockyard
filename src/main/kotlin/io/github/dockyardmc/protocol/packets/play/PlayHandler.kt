package io.github.dockyardmc.protocol.packets.play

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerMoveEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.AddPlayerInfoUpdateAction
import io.github.dockyardmc.player.PlayerInfoUpdate
import io.github.dockyardmc.player.PlayerUpdateProfileProperty
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.protocol.packets.play.serverbound.*
import io.github.dockyardmc.utils.Vector2
import io.netty.channel.ChannelHandlerContext
import log

class PlayHandler(var processor: PacketProcessor): PacketHandler(processor) {

    fun handleTeleportConfirmation(packet: ServerboundTeleportConfirmationPacket, connection: ChannelHandlerContext) {
        log("Received teleport confirmation packet")
     }

    fun handlePlayerPositionAndRotationUpdates(packet: ServerboundSetPlayerPositionPacket, connection: ChannelHandlerContext) {
        val player = processor.player
        this.handlePlayerPositionAndRotationUpdates(Location(packet.x, packet.y, packet.z, player.location.yaw, player.location.pitch), packet.isOnGround, connection)
    }

    fun handlePlayerPositionAndRotationUpdates(packet: ServerboundSetPlayerPositionAndRotationPacket, connection: ChannelHandlerContext) {
        this.handlePlayerPositionAndRotationUpdates(Location(packet.x, packet.y, packet.z, packet.yaw, packet.pitch), packet.isOnGround, connection)
    }

    fun handlePlayerPositionAndRotationUpdates(packet: ServerboundSetPlayerRotationPacket, connection: ChannelHandlerContext) {
        val player = processor.player
        this.handlePlayerPositionAndRotationUpdates(Location(player.location.x, player.location.y, player.location.z, packet.yaw, packet.pitch), packet.isOnGround, connection, true)
    }

    fun handlePlayerPositionAndRotationUpdates(location: Location, isOnGround: Boolean, connection: ChannelHandlerContext, isOnlyHeadMovement: Boolean = false) {
        val player = processor.player
        val oldLocation = player.location

        val event = PlayerMoveEvent(oldLocation, location, player, isOnlyHeadMovement)
        Events.dispatch(event)

        player.location = location
        player.isOnGround = isOnGround

        if(isOnlyHeadMovement) {
            val packet = ClientboundUpdateEntityRotationPacket(player, Vector2(player.location.yaw, player.location.pitch))
            player.sendToViewers(packet)
        } else {
            val packet = ClientboundUpdateEntityPositionAndRotationPacket(player, oldLocation)
            player.sendToViewers(packet)
        }
        val headRotPacket = ClientboundSetHeadYawPacket(player)
        player.sendToViewers(headRotPacket)
    }

    fun handleKeepAlive(packet: ServerboundKeepAlivePacket, connection: ChannelHandlerContext) {
        processor.respondedToLastKeepAlive = true
    }

    fun handlePluginMessage(packet: ServerboundPlayPluginMessagePacket, connection: ChannelHandlerContext) {
        log(packet.channel)
    }
}