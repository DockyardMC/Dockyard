package io.github.dockyardmc.entities

import cz.lukynka.Bindable
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.world.World

class Pig(
    override var location: Location,
    override var world: World,
    override var type: EntityType = EntityTypes.PIG,
    override var inventorySize: Int = 0
) : Entity() {
    override var health: Bindable<Float> = Bindable(20f)
}