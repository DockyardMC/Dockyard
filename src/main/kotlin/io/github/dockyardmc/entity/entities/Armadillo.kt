package io.github.dockyardmc.entity.entities

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

open class Armadillo(location: Location): Entity(location) {
    override var type: EntityType = EntityTypes.ARMADILLO
    override val health: Bindable<Float> = bindablePool.provideBindable(12f)
    override var inventorySize: Int = 0

    enum class State {
        IDLE,
        ROLLING,
        SCARED,
        UNROLLING
    }
}