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
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.team.Team
import io.github.dockyardmc.team.TeamManager
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.toVector3f
import io.github.dockyardmc.world.World
import java.util.UUID

abstract class Entity {
    abstract var entityId: Int
    abstract var uuid: UUID
    abstract var type: EntityType
    abstract var location: Location
    abstract var velocity: Vector3
    abstract var viewers: MutableList<Player>
    abstract var hasGravity: Boolean
    abstract var canBeDamaged: Boolean
    abstract var hasCollision: Boolean
    abstract var world: World
    abstract var displayName: String
    abstract var isOnGround: Boolean
    abstract var metadata: BindableMutableList<EntityMetadata>
    abstract var pose: Bindable<EntityPose>

    var team: Team? = null
        set(value) {
            if (value !in TeamManager.teams.values) {
                throw IllegalArgumentException("This team is not registered!")
            }

            this.team?.entities?.remove(this)
            field = value
            value?.entities?.addIfNotPresent(this)
        }

    open fun addViewer(player: Player) {
        val event = EntityViewerAddEvent(this, player)
        Events.dispatch(event)
        if(event.cancelled) return

        val entitySpawnPacket = ClientboundSpawnEntityPacket(entityId, uuid, type.id, location, 90f, 0, velocity)
        player.sendPacket(entitySpawnPacket)

        viewers.add(player)
        DockyardServer.broadcastMessage("<gray>Added viewer for ${this}: <lime>$player")
    }

    open fun removeViewer(player: Player, isDisconnect: Boolean) {
        val event = EntityViewerRemoveEvent(this, player)
        Events.dispatch(event)
        if(event.cancelled) return

        viewers.remove(player)
        DockyardServer.broadcastMessage("<gray>Removed viewer for ${this}: <red>$player")
        val entityDespawnPacket = ClientboundEntityRemovePacket(this)
        player.sendPacket(entityDespawnPacket)
    }

    //TODO move to bindable
    open fun setEntityVelocity(velocity: Vector3) {
        val packet = ClientboundSetVelocityPacket(this, velocity)
        viewers.sendPacket(packet)
        sendSelfPacketIfPlayer(packet)

    }

    //TODO make this work
    open fun lookAt(target: Entity) {
        val newLoc = this.location.setDirection(target.location.subtract(this.location).toVector3f())
        this.location = newLoc

        this.location.yaw = (newLoc.yaw % 360) * 256 / 360
        val packet = ClientboundEntityTeleportPacket(this)
        viewers.sendPacket(packet)
    }

    open fun sendMetadataPacketToViewers() {
        val packet = ClientboundEntityMetadataPacket(this)
        viewers.sendPacket(packet)
    }

    open fun calculateBoundingBox(): BoundingBox {
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

    private fun sendSelfPacketIfPlayer(packet: ClientboundPacket) {
        if(this is Player) this.sendPacket(packet)
    }
}