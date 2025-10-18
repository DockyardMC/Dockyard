package io.github.dockyardmc.entity.entities

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

open class Salmon(location: Location): Entity(location) {
    override var type: EntityType = EntityTypes.SALMON
    override val health: Bindable<Float> = bindablePool.provideBindable(20f)

    enum class Size {
        SMALL,
        MEDIUM,
        LARGE
    }
}