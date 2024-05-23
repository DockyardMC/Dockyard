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
import io.netty.channel.ChannelHandlerContext
import log

class PlayHandler(var processor: PacketProcessor): PacketHandler(processor) {

    fun handleTeleportConfirmation(packet: ServerboundTeleportConfirmationPacket, connection: ChannelHandlerContext) {
        val player = processor.player
        val playerInfo = PlayerInfoUpdate(player.uuid, AddPlayerInfoUpdateAction(PlayerUpdateProfileProperty(player.username, mutableListOf(player.profile!!.properties[0]))))
        val playerInfoUpdatePacket = ClientboundPlayerInfoUpdatePacket(1, mutableListOf(playerInfo))
        connection.sendPacket(playerInfoUpdatePacket)

        val worldBorder = player.world!!.worldBorder
        val worldBorderPacket = ClientboundInitializeWorldBorderPacket(worldBorder.diameter, worldBorder.diameter, 0, worldBorder.warningBlocks, worldBorder.warningTime)
        connection.sendPacket(worldBorderPacket)

//        val tickingStatePacket = ClientboundSetTickingStatePacket(DockyardServer.tickRate, false)
//        connection.sendPacket(tickingStatePacket)

        val chunkCenterChunkPacket = ClientboundSetCenterChunkPacket(0, 0)
        connection.sendPacket(chunkCenterChunkPacket)

        //TODO: add chunks lol
        val gameEventPacket = ClientboundPlayerGameEventPacket(GameEvent.START_WAITING_FOR_CHUNKS, 0f)
        connection.sendPacket(gameEventPacket)
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
    }

    fun handleKeepAlive(packet: ServerboundKeepAlivePacket, connection: ChannelHandlerContext) {
        processor.respondedToLastKeepAlive = true
    }

    fun handlePluginMessage(packet: ServerboundPlayPluginMessagePacket, connection: ChannelHandlerContext) {
        log(packet.channel)
    }
}