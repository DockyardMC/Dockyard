package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

class LightningBolt(location: Location) : Entity(location) {
    override var type: EntityType = EntityTypes.LIGHTNING_BOLT
    override val health: Bindable<Float> = bindablePool.provideBindable(0f)
    override var inventorySize: Int = 0
}