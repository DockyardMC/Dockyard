package io.github.dockyardmc.entity

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.bindables.BindableMutableList
import io.github.dockyardmc.events.EntityViewerAddEvent
import io.github.dockyardmc.events.EntityViewerRemoveEvent
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityMetadataPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityRemovePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSpawnEntityPacket
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.world.World
import java.util.UUID

interface Entity {
    var entityId: Int
    var uuid: UUID
    var type: EntityType
    var location: Location
    var velocity: Vector3
    var viewers: MutableList<Player>
    var hasGravity: Boolean
    var canBeDamaged: Boolean
    var hasCollision: Boolean
    var world: World
    var displayName: String
    var isOnGround: Boolean
    var metadata: BindableMutableList<EntityMetadata>
    var pose: Bindable<EntityPose>

    fun addViewer(player: Player) {

        val event = EntityViewerAddEvent(this, player)
        Events.dispatch(event)
        if(event.cancelled) return

        val entitySpawnPacket = ClientboundSpawnEntityPacket(entityId, uuid, type.id + 2, location, 90f, 0, velocity)
        player.sendPacket(entitySpawnPacket)

        viewers.add(player)
        DockyardServer.broadcastMessage("<gray>Added viewer for ${this}: <lime>$player")
    }

    fun removeViewer(player: Player, isDisconnect: Boolean) {

        val event = EntityViewerRemoveEvent(this, player)
        Events.dispatch(event)
        if(event.cancelled) return

        viewers.remove(player)
        DockyardServer.broadcastMessage("<gray>Removed viewer for ${this}: <red>$player")
        val entityDespawnPacket = ClientboundEntityRemovePacket(this)
        player.sendPacket(entityDespawnPacket)
    }

    fun sendViewersMedataPacket() {
        val packet = ClientboundEntityMetadataPacket(this)
        viewers.sendPacket(packet)
    }
}