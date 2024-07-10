package io.github.dockyardmc.entities.vanilla

import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.bindables.BindableList
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.entities.EntityManager
import io.github.dockyardmc.entities.EntityMetadata
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.world.World
import java.util.*

class ItemDropEntity(
    override var location: Location,
    override var world: World = location.world,
    override var entityId: Int = EntityManager.entityIdCounter.incrementAndGet(),
    override var uuid: UUID = UUID.randomUUID(),
    override var type: EntityType = EntityTypes.ITEM,
    override var velocity: Vector3 = Vector3(),
    override var viewers: MutableList<Player> = mutableListOf(),
    override var hasGravity: Boolean = true,
    override var isInvulnerable: Boolean = true,
    override var hasCollision: Boolean = false,
    override var displayName: String = "",
    override var isOnGround: Boolean = true,
    override var metadata: BindableList<EntityMetadata> = BindableList(),
    override var pose: Bindable<EntityPose> = Bindable(EntityPose.STANDING),
    override var inventorySize: Int = 0
): Entity() {
    override var health: Bindable<Float> = Bindable(9999f)
}