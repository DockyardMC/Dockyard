package io.github.dockyardmc.entities

import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.bindables.BindableList
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.world.World
import java.util.*

class Sheep(
    override var entityId: Int = EntityManager.entityIdCounter.incrementAndGet(),
    override var uuid: UUID = UUID.randomUUID(),
    override var type: EntityType = EntityTypes.SHEEP,
    override var location: Location,
    override var velocity: Vector3 = Vector3(),
    override var viewers: MutableList<Player> = mutableListOf(),
    override var hasGravity: Boolean = true,
    override var isInvulnerable: Boolean = true,
    override var hasCollision: Boolean = true,
    override var world: World,
    override var displayName: String = "",
    override var isOnGround: Boolean = true,
    override var metadata: BindableList<EntityMetadata> = BindableList(),
    override var pose: Bindable<EntityPose> = Bindable(EntityPose.STANDING),
    override var inventorySize: Int = 0
) : Entity() {
    override var health: Bindable<Float> = Bindable(20f)
}