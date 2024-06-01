package io.github.dockyardmc.entity

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityMetadataPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundRemoveEntitiesPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSpawnEntityPacket
import io.github.dockyardmc.scroll.Component
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
    var metadata: MutableList<EntityMetadata>
    var pose: Bindable<EntityPose>

    fun addViewer(player: Player) {
        val entitySpawnPacket = ClientboundSpawnEntityPacket(entityId, uuid, type.id + 2, location, 90f, 0, velocity)
        player.sendPacket(entitySpawnPacket)
        viewers.add(player)
        DockyardServer.broadcastMessage("<gray>Added viewer for ${this}: <lime>$player")
    }

    fun removeViewer(player: Player, isDisconnect: Boolean) {
        viewers.remove(player)
        DockyardServer.broadcastMessage("<gray>Removed viewer for ${this}: <red>$player")
        val entityDespawnPacket = ClientboundRemoveEntitiesPacket(this)
        player.sendPacket(entityDespawnPacket)
    }

    fun sendMetadataUpdatePacket() {
        val packet = ClientboundEntityMetadataPacket(this)
        viewers.sendPacket(packet)
    }
}