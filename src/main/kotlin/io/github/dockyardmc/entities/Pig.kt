package io.github.dockyardmc.entities

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.bindables.BindableMutableList
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerMoveEvent
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Entities
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.world.World
import java.util.*

class Pig(
    override var entityId: Int = EntityManager.entityIdCounter.incrementAndGet(),
    override var uuid: UUID = UUID.randomUUID(),
    override var type: EntityType = Entities.PIG,
    override var location: Location,
    override var velocity: Vector3 = Vector3(),
    override var viewers: MutableList<Player> = mutableListOf(),
    override var hasGravity: Boolean = true,
    override var canBeDamaged: Boolean = true,
    override var hasCollision: Boolean = true,
    override var world: World,
    override var displayName: String = "",
    override var isOnGround: Boolean = true,
    override var metadata: BindableMutableList<EntityMetadata> = BindableMutableList(),
    override var pose: Bindable<EntityPose> = Bindable(EntityPose.STANDING)
) : Entity {

    init {
        Events.on<PlayerMoveEvent> {
            val dist = it.player.location.distance(location)
            if(dist < 6) {
                lookAt(it.player)
            }
        }
    }
}