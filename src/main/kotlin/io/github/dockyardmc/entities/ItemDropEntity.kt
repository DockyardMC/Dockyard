package io.github.dockyardmc.entities

import cz.lukynka.Bindable
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.world.World

class ItemDropEntity(
    override var location: Location,
    override var world: World = location.world,
    override var type: EntityType = EntityTypes.ITEM,
    override var inventorySize: Int = 0
): Entity() {
    override var health: Bindable<Float> = Bindable(9999f)
}