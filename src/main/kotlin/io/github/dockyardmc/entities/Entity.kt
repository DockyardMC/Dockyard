package io.github.dockyardmc.entities

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
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.toVector3
import io.github.dockyardmc.utils.toVector3f
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

        val entitySpawnPacket = ClientboundSpawnEntityPacket(entityId, uuid, type.id, location, 90f, 0, velocity)
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

    //TODO make this work
    fun lookAt(target: Entity) {
        val newLoc = this.location.setDirection(target.location.subtract(this.location).toVector3f())
        this.location = newLoc

        this.location.yaw = (newLoc.yaw % 360) * 256 / 360
        val packet = ClientboundEntityTeleportPacket(this)
        viewers.sendPacket(packet)
    }

    fun sendViewersMedataPacket() {
        val packet = ClientboundEntityMetadataPacket(this)
        viewers.sendPacket(packet)
    }

    fun calculateBoundingBox(): BoundingBox {
        val width = type.width
        val height = type.height
        return BoundingBox(
            location.x - width / 2,
            location.x + width / 2,
            location.y - height / 2,
            location.y + height /2,
            location.z - width / 2,
            location.z + width / 2
        )
    }

    data class BoundingBox(
        val minX: Double,
        val maxX: Double,
        val minY: Double,
        val maxY: Double,
        val minZ: Double,
        val maxZ: Double
    )
}