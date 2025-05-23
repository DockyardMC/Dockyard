package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

class Squid(location: Location): Entity(location) {
    override var type: EntityType = EntityTypes.SQUID
    override val health: Bindable<Float> = bindablePool.provideBindable(10f)
    override var inventorySize: Int = 0
}