package io.github.dockyardmc.entity

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
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
    var displayName: Component
    var isOnGround: Boolean

    fun addViewer(player: Player) {
        val entitySpawnPacket = ClientboundSpawnEntityPacket(entityId, uuid, type.id + 2, location, 90f, 0, velocity)
        player.sendPacket(entitySpawnPacket)
        viewers.add(player)
    }

    fun removeViewer(player: Player, isDisconnect: Boolean) {
        val entityDespawnPacket = ClientboundRemoveEntitiesPacket(this)
        player.sendPacket(entityDespawnPacket)
        viewers.remove(player)
    }
}